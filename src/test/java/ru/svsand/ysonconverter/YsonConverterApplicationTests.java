package ru.svsand.ysonconverter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.svsand.ysonconverter.converter.ConverterYsonToJson;

@SpringBootTest
class YsonConverterApplicationTests {

    @MockitoBean
    private ConverterYsonToJson converterYsonToJson;

    @Test
    void contextLoads() {
    }
}
