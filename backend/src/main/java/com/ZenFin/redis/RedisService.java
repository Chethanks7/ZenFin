package com.ZenFin.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Service
@RequiredArgsConstructor
@Setter
@Getter
public class RedisService {
    
    private final RedisTemplate redisTemplate ; 

    public void set(String key , Object dataClass, Long expTime ){
        redisTemplate.opsForValue().set(key,dataClass, expTime,TimeUnit.SECONDS );
    }

    public <T>T get(String key, Class<T> entityClass) throws Exception {
        Object obj = redisTemplate.opsForValue().get(key);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(obj.toString(), entityClass);
    }
 
}
