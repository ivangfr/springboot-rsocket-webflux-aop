package com.mycompany.movieclientui.controller;

import com.mycompany.movieclientui.controller.dto.MovieUpdateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MovieClientUiController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("movies.updates")
    public void moviesUpdates(MovieUpdateMessage movieUpdateMessage,
                              @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                              @Header(CONTENT_TYPE) String contentType) {
        simpMessagingTemplate.convertAndSend("/topic/movies/updates", movieUpdateMessage);
    }

    private static final String RSOCKET_FRAME_TYPE = RSocketFrameTypeMessageCondition.FRAME_TYPE_HEADER;
    private static final String CONTENT_TYPE = "contentType";
}
