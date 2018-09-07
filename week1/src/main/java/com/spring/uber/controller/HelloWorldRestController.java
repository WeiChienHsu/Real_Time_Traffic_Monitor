package com.spring.uber.controller;

import com.spring.uber.domain.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldRestController {

    @RequestMapping("/")
    public String welcome() {
        return "Hello World! Spring Uber.";
    }

    @RequestMapping(value = "/hello/{yourName}", method = RequestMethod.GET)
    public Message showMessage(@PathVariable String yourName) {
        Message msg = new Message(yourName, "Hello " );
        return msg;
    }

}
