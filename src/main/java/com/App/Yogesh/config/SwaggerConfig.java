package com.App.Yogesh.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Configuration
@Slf4j  // Lombok logger
public class SwaggerConfig {

    @Bean
    public OpenAPI myCustomConfig() {
        log.info("Configuring Swagger API documentation...");  // Log when Swagger config is initialized

        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Social App Api")
                        .description("By Yogesh") // API description
                )
                // Uncomment below lines if you want to add different environments as servers
                //.servers(List.of(new Server().url("http://localhost:8081").description("local"),
                //                  new Server().url("http://localhost:8080").description("live")))

                // Uncomment below lines if you want to categorize the API endpoints by tags
                //.tags(List.of(new Tag().name("SignUp&In Apis"),
                //               new Tag().name("AdminController"),
                //               new Tag().name("ForgetPasswordApis"),
                //               new Tag().name("UserApi"),
                //               new Tag().name("PostApis"),
                //               new Tag().name("CommentApis"),
                //               new Tag().name("StoryApis"),
                //               new Tag().name("ReelsApis")))

                // Adding security with bearer token (JWT)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                ));

        log.info("Swagger configuration completed successfully");  // Log successful Swagger setup

        return openAPI;
    }
}
