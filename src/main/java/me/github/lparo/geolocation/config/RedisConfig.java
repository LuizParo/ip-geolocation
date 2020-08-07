package me.github.lparo.geolocation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

/**
 * Configuration file responsible for creating Spring beans related to Redis.
 */
@Profile("!integration-test")
@Configuration
public class RedisConfig {
    private final String redisHost;
    private final int redisPort;

    public RedisConfig(@Value("${REDIS_HOST:localhost}") String redisHost,
                       @Value("${REDIS_PORT:6379}") int redisPort) {
        this.redisHost = redisHost;
        this.redisPort = redisPort;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisHost);
        configuration.setPort(redisPort);

        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, ? extends Serializable> redisTemplate() {
        final RedisTemplate<String, ? extends Serializable> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());

        return template;
    }
}
