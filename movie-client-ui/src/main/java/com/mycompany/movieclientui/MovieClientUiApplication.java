package com.mycompany.movieclientui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.ProxyBits;

@AotProxyHint(
        targetClass = com.mycompany.movieclientui.controller.MovieClientUiController.class,
        proxyFeatures = ProxyBits.IS_STATIC)
@SpringBootApplication
public class MovieClientUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieClientUiApplication.class, args);
    }
}
