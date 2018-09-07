package com.spring.uber.service;

import com.spring.uber.domain.Message;

public class HelloWorldService {

    public Message getMessage(String name, String text) {
        Message msg = new Message(name, "Hello " + name);
        return msg;
    }
}
