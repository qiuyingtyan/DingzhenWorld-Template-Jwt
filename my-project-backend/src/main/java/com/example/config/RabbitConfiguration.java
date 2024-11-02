package com.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    // 定义一个名为messageConverter的Bean，返回一个Jackson2JsonMessageConverter对象
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 定义一个名为mailQueue的队列
    @Bean("mailQueue")
    public Queue queue(){
        // 创建一个名为mail的持久化队列
        return QueueBuilder
                .durable("mail")
                .build();
    }
}