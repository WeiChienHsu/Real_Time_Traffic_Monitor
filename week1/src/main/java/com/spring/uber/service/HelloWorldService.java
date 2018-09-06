package com.spring.uber.service;

import com.spring.uber.domain.Message;
import org.springframework.context.annotation.Bean;


public class HelloWorldService {

    @Bean
    public Message getMessage(String name, String text) {
        Message msg = new Message(name, "Hello " );
        return msg;
    }
}
