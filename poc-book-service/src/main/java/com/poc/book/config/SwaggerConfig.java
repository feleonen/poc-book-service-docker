package com.poc.book.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(("com.poc.book.web.controller")))
                .paths(PathSelectors.any())
                .build()
                .apiInfo((getApiInfo()));
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "POC Google-Book API",
                "POC Google-Book API Services",
                "1.0",
                "",
                new Contact("", "", ""),
                "LICENSE",
                "LICENSE_URL",
                Collections.emptyList()
        );
    }
}
