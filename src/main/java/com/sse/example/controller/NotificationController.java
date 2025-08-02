/**
 * Author: Vinod Jagwani
 */
package com.sse.example.controller;

import com.sse.example.dto.Notification;
import com.sse.example.service.NotificationService;
import java.time.Duration;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NotificationController {

    NotificationService notificationService;

    @PostMapping
    public Mono<Void> sendNotification(@RequestBody final Notification notification) {
        return notificationService.sendNotification(notification).then();
    }

    @GetMapping(value = "/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Notification>> streamNotificationsWithHeartbeat(@PathVariable final String userId) {
        final Flux<Notification> notificationStream = notificationService.getNotificationsForUser(userId)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .doBeforeRetry(retry ->
                                log.warn("Retrying connection (attempt {})", retry.totalRetries() + 1))
                );
        final Flux<ServerSentEvent<Notification>> sseStream = notificationStream
                .map(notification -> ServerSentEvent.builder(notification)
                        .retry(Duration.ofSeconds(5))
                        .build())
                .onErrorResume(e -> {
                    log.error("Stream error: {}", e.getMessage());
                    return Flux.just(
                            ServerSentEvent.<Notification>builder()
                                    .event("error")
                                    .data(createErrorNotification(e, userId))
                                    .retry(Duration.ofSeconds(5))
                                    .build()
                    );
                });
        return sseStream.mergeWith(heartbeat());
    }

    private Notification createErrorNotification(final Throwable e, final String userId) {
        return new Notification(
                "error-" + Instant.now().toEpochMilli(),
                userId,
                "Connection error: " + e.getMessage(),
                Instant.now().toString(),
                false
        );
    }

    private Flux<ServerSentEvent<Notification>> heartbeat() {
        return Flux.interval(Duration.ofSeconds(15))
                .map(i -> ServerSentEvent.<Notification>builder()
                        .event("heartbeat")
                        .comment("ping")
                        .retry(Duration.ofSeconds(5))
                        .build());
    }
}