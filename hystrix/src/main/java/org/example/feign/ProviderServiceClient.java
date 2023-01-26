package org.example.feign;

import org.example.entity.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@FeignClient(value = "provider")
public interface ProviderServiceClient {

    @GetMapping("/student/findAll")
    public Collection<Student> findAll();

    @GetMapping("/port/index")
    public String index();
}
