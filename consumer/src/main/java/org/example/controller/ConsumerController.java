package org.example.controller;

import org.example.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/findAll")
    public Collection<Student> findAll() {
        // 这里通过 restTemplate 来调用 eureka-client 中的服务
        return restTemplate.getForEntity("http://localhost:8010/student/findAll", Collection.class).getBody();
    }

    /**
     * 使用 restTemplate 的另一种写法
     * restTemplate.getForObject 是对 restTemplate.getForEntity 进一步封装，适合只关心消息体内容的情况
     */
    @GetMapping("/findAll2")
    public Collection<Student> findAll2() {
        return restTemplate.getForObject("http://localhost:8010/student/findAll", Collection.class);
    }

    @GetMapping("/findById/{id}")
    public Student findById(@PathVariable("id") long id) {
        return restTemplate.getForEntity("http://localhost:8010/student/findById/{id}", Student.class, id).getBody();
    }

    @PostMapping("/save")
    public void save(@RequestBody Student student) {
        restTemplate.postForEntity("http://localhost:8010/student/save", student, null).getBody();
    }

    @PostMapping("/update")
    public void update(@RequestBody Student student) {
        restTemplate.put("http://localhost:8010/student/update", student);
    }

    @DeleteMapping("/deleteById/{id}")
    public void deleteById(@PathVariable("id") long id) {
        restTemplate.delete("http://localhost:8010/student/deleteById/{id}", id);
    }
}
