package com.yu.ymind.manager;

import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Cos 操作测试
 *
 *
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private CosManager cosManager;

    String filePath="G:\\object\\YMind\\YMind-backend\\src\\test\\java\\com\\yu\\ymind\\manager\\test.txt";

    @Test
    void putObject() {
        cosManager.putObject("test", filePath);
    }
}