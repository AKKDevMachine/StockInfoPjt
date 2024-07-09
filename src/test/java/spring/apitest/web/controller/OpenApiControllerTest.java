package spring.apitest.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.apitest.service.OpenApiService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenApiControllerTest {
    @Autowired
    OpenApiService openApiService;
    @Autowired OpenApiController openApiController;

    @Test
    void test() throws IOException {
        openApiController.getStockRank();
    }

    @Test
    void getApiSchedule() throws IOException {
        openApiService.getApiSchedule();
    }
}