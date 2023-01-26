package org.example.feign;

import org.example.entity.Student;
import org.example.feign.errorimpl.ProviderServiceError;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@FeignClient(
        value = "provider",  // 声明所要访问哪个微服务，这里访问 provider 服务
        fallback = ProviderServiceError.class  // 远程调用出现问题时，会去调用这个错误处理逻辑
)
public interface ProviderServiceClient {

    @GetMapping("/student/findAll")
    public Collection<Student> findAll();

    @GetMapping("/port/index")
    public String index();
}
