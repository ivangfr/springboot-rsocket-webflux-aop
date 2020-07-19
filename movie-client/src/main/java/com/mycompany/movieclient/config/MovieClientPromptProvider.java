package com.mycompany.movieclient.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class MovieClientPromptProvider implements PromptProvider {

    @Override
    public AttributedString getPrompt() {
        return new AttributedString("movie-client> ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }

}
