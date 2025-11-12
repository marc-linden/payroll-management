package com.example.payroll.web.config;

import java.util.*;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Demo payroll application")
            .version("1.0.0")
            .description("Manages monthly working log of employees"))
        .servers(List.of(
            new Server().url("http://localhost:8080").description("Local Environment")
        ));
  }
}
