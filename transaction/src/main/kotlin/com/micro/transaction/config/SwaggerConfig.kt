package com.micro.transaction.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig  {

    @Bean
    fun api(): Docket? {
        return Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.micro.transaction.controller"))
            .paths(PathSelectors.any())
            .build()
    }
    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
            .title("My Transaction Service APIs")
            .description("My Transaction Service APIs description.")
            .version("1.0.0")
            .build()
    }
}