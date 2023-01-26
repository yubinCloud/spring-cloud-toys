package org.example.controller;

import org.example.entity.Student;
import org.example.feign.ProviderServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/feign")
public class FeignController {

    @Autowired
    private ProviderServiceClient providerServiceClient;

    @GetMapping("/findAll")
    public Collection<Student> findAll() {
        return providerServiceClient.findAll();
    }

    @GetMapping("/index")
    public String index() {
        return providerServiceClient.index();
    }
}
