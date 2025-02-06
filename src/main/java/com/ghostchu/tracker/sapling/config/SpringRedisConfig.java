package com.ghostchu.tracker.sapling.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SpringRedisConfig {

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory, ObjectMapper mapper) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 创建自定义的 GenericJackson2JsonRedisSerializer
        GenericJackson2JsonRedisSerializer serializer = new CustomGenericJackson2JsonRedisSerializer(mapper);

        // 配置序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    static class CustomGenericJackson2JsonRedisSerializer extends GenericJackson2JsonRedisSerializer {
        public CustomGenericJackson2JsonRedisSerializer(ObjectMapper mapper) {
            super(mapper);
        }

        @Override
        public Object deserialize(byte[] source) throws SerializationException {
            // 临时保存原始类加载器
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                // 关键步骤：设置当前线程的类加载器为应用类加载器（RestartClassLoader）
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                // 此时 Jackson 会使用当前线程的类加载器解析类型
                return super.deserialize(source);
            } finally {
                // 恢复原始类加载器（避免影响其他逻辑）
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

}
