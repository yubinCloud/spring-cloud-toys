package org.example.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/native")
public class NativeConfigController {

    @Value("${server.port}")
    private String port;  // 这里读取到的值都是从 native config server 中获得的

    @Value("${foo}")
    private String foo;

    @GetMapping("/index")
    public String index() {
        return this.port + "-" + this.foo;
    }
}
