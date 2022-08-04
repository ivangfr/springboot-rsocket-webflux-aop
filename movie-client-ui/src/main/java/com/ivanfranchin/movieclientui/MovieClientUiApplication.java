package com.ivanfranchin.movieclientui;

import com.ivanfranchin.movieclientui.controller.MovieClientUiController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.ProxyBits;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;

@NativeHint(
        aotProxies = @AotProxyHint(
                targetClass = MovieClientUiController.class,
                proxyFeatures = ProxyBits.IS_STATIC
        ),
        types = @TypeHint(
                types = TomcatRequestUpgradeStrategy.class
        )
)
@SpringBootApplication
public class MovieClientUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieClientUiApplication.class, args);
    }
}
