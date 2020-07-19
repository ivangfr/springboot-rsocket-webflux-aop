package com.mycompany.movieclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "movie-server")
@Validated
public class MovieServerProperties {

    @NotBlank
    private String host;

    @NotNull
    private Rest rest;

    @NotNull
    private RSocket rsocket;

    @Data
    @Validated
    public static class Rest {

        @NotNull
        @Min(8080)
        @Max(8089)
        private Integer port;
    }

    @Data
    @Validated
    public static class RSocket {

        @NotNull
        @Min(7000)
        @Max(7009)
        private Integer port;
    }
}
