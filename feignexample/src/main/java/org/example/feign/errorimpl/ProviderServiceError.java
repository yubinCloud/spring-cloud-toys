package org.example.feign.errorimpl;

import org.example.entity.Student;
import org.example.feign.ProviderServiceClient;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 定义 Feign 的容错逻辑
 */
@Component
public class ProviderServiceError implements ProviderServiceClient {
    @Override
    public Collection<Student> findAll() {
        return null;
    }

    @Override
    public String index() {
        return "服务器维护中...";
    }
}
