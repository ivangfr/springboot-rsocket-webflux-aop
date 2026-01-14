package com.ivanfranchin.movieclientshell.chronometer;

import org.springframework.shell.core.command.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class ChronometerCommands {

    private static final Chronometer chronometer = new Chronometer();

    @Command(name = "start-chronometer", group = "Controller commands")
    public void startChronometer() {
        chronometer.start();
    }

    @Command(name = "stop-chronometer", group = "Controller commands")
    public String stopChronometer() {
        return chronometer.stop();
    }

    private static class Chronometer {
        private Long t;

        public void start() {
            t = System.currentTimeMillis();
        }

        public String stop() {
            return String.format("---\nExecution time: %sms\n---", System.currentTimeMillis() - t);
        }
    }
}
