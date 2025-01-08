package com.ZenFin.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @SuppressWarnings("unchecked")
    @Bean
     public RedisTemplate redisTemplate(RedisConnectionFactory factory){

        RedisTemplate template = new RedisTemplate<>();
         template.setConnectionFactory(factory);

         template.setKeySerializer(new StringRedisSerializer());
         template.setValueSerializer(new StringRedisSerializer());

         return template ; 
    }

}
