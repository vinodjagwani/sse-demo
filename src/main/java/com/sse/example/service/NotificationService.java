package com.sse.example.service;

import com.sse.example.dto.Notification;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.connection.ReactiveSubscription.Message;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    ChannelTopic notificationTopic;

    ReactiveRedisTemplate<String, Notification> redisTemplate;

    public Mono<Long> sendNotification(final Notification notification) {
        return redisTemplate.convertAndSend(notificationTopic.getTopic(), notification);
    }

    public Flux<Notification> getNotificationsForUser(final String userId) {
        return redisTemplate.listenToChannel(notificationTopic.getTopic())
                .map(Message::getMessage)
                .filter(notification -> userId.equals(notification.userId()));
    }
}