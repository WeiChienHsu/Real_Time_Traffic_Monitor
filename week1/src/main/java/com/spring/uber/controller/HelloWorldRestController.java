package com.spring.uber.controller;

import com.spring.uber.domain.Message;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorldRestController {

    @RequestMapping("/")
    public String welcome() {
        return "Hello Spring Uber";
    }

    @RequestMapping(value = "/hello/{yourName}", method = RequestMethod.GET)
    public Message showMessage(@PathVariable String yourName) {
        Message msg = new Message(yourName, "Hello ");
        return msg;
    }

}
