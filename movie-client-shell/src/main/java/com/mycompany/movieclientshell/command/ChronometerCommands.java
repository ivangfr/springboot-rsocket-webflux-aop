package com.mycompany.movieclientshell.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ChronometerCommands {

    private static final Chronometer chronometer = new Chronometer();

    @ShellMethod("Start chronometer")
    public void startChronometer() {
        chronometer.start();
    }

    @ShellMethod("Stop chronometer")
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
