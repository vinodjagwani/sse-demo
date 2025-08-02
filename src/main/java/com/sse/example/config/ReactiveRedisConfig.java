/**
 * Author: Vinod Jagwani
 */
package com.sse.example.config;

import com.sse.example.dto.Notification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ReactiveRedisConfig {

    @Bean
    public ChannelTopic notificationTopic() {
        return new ChannelTopic("notifications");
    }

    @Bean
    public ReactiveRedisTemplate<String, Notification> reactiveRedisTemplate(
            final ReactiveRedisConnectionFactory factory) {
        final Jackson2JsonRedisSerializer<Notification> serializer =
                new Jackson2JsonRedisSerializer<>(Notification.class);
        final RedisSerializationContext.RedisSerializationContextBuilder<String, Notification> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        final RedisSerializationContext<String, Notification> context =
                builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
