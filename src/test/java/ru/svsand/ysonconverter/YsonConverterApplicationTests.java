package ru.svsand.ysonconverter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class YsonConverterApplicationTests {

    @MockitoBean
    private Runner runner;

    @Test
    void contextLoads() {
    }
}
