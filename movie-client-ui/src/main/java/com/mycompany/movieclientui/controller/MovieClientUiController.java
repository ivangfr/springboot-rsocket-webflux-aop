package com.mycompany.movieclientui.controller;

import com.mycompany.movieclientui.controller.dto.MovieUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MovieClientUiController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("movies.updates")
    public void moviesUpdates(MovieUpdateMessage movieUpdateMessage,
                              @Header("rsocketFrameType") String rsocketFrameType,
                              @Header("contentType") String contentType) {
        simpMessagingTemplate.convertAndSend("/topic/movies/updates", movieUpdateMessage);
    }

}
