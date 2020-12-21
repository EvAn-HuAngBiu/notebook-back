package com.notebook.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project: notebook
 * File: DirectRabbitConfig
 *
 * @author evan
 * @date 2020/12/17
 */
@EnableRabbit
@Configuration
public class DirectRabbitConfig {
    @Bean
    public Queue imageAuditQueue() {
        return new Queue("image", true, false, false);
    }

    @Bean
    public Queue textAuditQueue() {
        return new Queue("text", true, false, false);
    }

    @Bean
    public DirectExchange auditExchange() {
        return new DirectExchange("audit", true, false);
    }

    @Bean
    public Binding imageAuditBinding() {
        return BindingBuilder.bind(imageAuditQueue()).to(auditExchange()).with("image");
    }

    @Bean
    public Binding textAuditBinding() {
        return BindingBuilder.bind(textAuditQueue()).to(auditExchange()).with("text");
    }
}
