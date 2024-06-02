package com.yu.ymind.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration//这个注解用于表示一个配置类，Spring将会扫描这个类以及它所在的包，找出所有的 @Bean 注解，并把这些bean添加到Spring容器中。
@ConfigurationProperties(prefix = "ai")//这个注解用于将配置文件中的属性绑定到类的字段上。prefix 属性指定了配置属性前缀，Spring将会自动将配置文件中以 "ai" 开头的属性绑定到这个类的字段上。
@Data//这个注解来自 Lombok 库，它用于自动生成 getter、setter、equals、hashCode 和 toString 方法。
public class AIConfig {

    private String apiKey;

    @Bean//这个注解用于声明一个Bean
    public ClientV4 getClientV4() {
        return new ClientV4.Builder(apiKey).build();
    }
}
