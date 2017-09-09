package com.jrsoftlearning.microservices.basics;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class CompletableFuturesJoinedIntoPipeline {

    @Test
    public void pipeline() {
        CompletableFuture.supplyAsync(this::message).
                thenApply(this::beautify)
                .thenAccept(this::consumeMessage)
                .thenRun(this::finalAction);
    }

    String message() {
        return "hey duke: " + System.currentTimeMillis();
    }

    String beautify(String input) {
        return "+ " + input + " +";
    }

    void consumeMessage(String message) {
        System.out.println("message = " + message );
    }

    void finalAction() {
        System.out.println("Clean up!");
    }

}
