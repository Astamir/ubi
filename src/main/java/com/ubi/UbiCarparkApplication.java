package com.ubi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableRetry
@Slf4j
public class UbiCarparkApplication {
  public static void main(String[] args) {
    SpringApplication.run(UbiCarparkApplication.class, args);
  }
}
