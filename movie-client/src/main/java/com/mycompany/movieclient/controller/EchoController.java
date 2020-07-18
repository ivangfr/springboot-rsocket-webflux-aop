package com.mycompany.movieclient.controller;

import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class EchoController {

    private final RSocketRequester rSocket;

    @GetMapping(value = "/echo/{message}")
    public Publisher<String> echo(@PathVariable("message") String message) {
        return rSocket.route("requestresponse").data(message).retrieveMono(String.class);
    }

}
