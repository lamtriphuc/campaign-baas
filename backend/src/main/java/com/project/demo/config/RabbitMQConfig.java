package com.project.demo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    public static final String FLASH_SALE_QUEUE = "flash-sale-queue";

    @Bean
    public Queue flashSaleQueue() {
        // Tham số 'true' (durable): Đảm bảo hàng đợi không bị mất khi RabbitMQ khởi động lại
        return new Queue(FLASH_SALE_QUEUE);
    }

    // Cấu hình chuyển đổi Object Java sang JSON để dễ đọc trên giao diện Web
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
