package me.github.lparo.geolocation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.containers.GenericContainer;

import java.io.Serializable;

@Configuration
@Profile("integration-test")
public class RedisConfigForTesting {

    @Bean
    public GenericContainer<?> createRedisContainer() {
        final GenericContainer<?> container = new GenericContainer<>("redis:6.0.6").withExposedPorts(6379);
        container.start();

        return container;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(GenericContainer<?> redisContainer) {
        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisContainer.getHost());
        configuration.setPort(redisContainer.getFirstMappedPort());

        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisTemplate<String, ? extends Serializable> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, ? extends Serializable> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);

        return template;
    }
}
