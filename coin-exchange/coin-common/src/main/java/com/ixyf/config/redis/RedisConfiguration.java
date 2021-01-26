package com.ixyf.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    /**
     * redisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> stringObjectRedisTemplate = new RedisTemplate<>();
        stringObjectRedisTemplate.setConnectionFactory(redisConnectionFactory);
        // redis key和value的序列化
        final StringRedisSerializer stringKeySerializer = new StringRedisSerializer();
        final GenericJackson2JsonRedisSerializer redisValueSerializer = new GenericJackson2JsonRedisSerializer();
        stringObjectRedisTemplate.setKeySerializer(stringKeySerializer);
        stringObjectRedisTemplate.setValueSerializer(redisValueSerializer);
        stringObjectRedisTemplate.setHashKeySerializer(stringKeySerializer);
        stringObjectRedisTemplate.setHashValueSerializer(redisValueSerializer);


        return stringObjectRedisTemplate;

    }
}
