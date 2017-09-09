package com.jrsoftlearning.microservices.basics;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class CompletableFuturesJoinedIntoPipelineTest {

    @Test
    public void pipeline() {
        CompletableFuture.supplyAsync(this::message).
                thenApply(this::beautify)
                .thenAccept(this::consumeMessage)
                .thenRun(this::finalAction);
    }

    @Test
    public void combinePipelines() {
        CompletableFuture<String> first = CompletableFuture.supplyAsync(this::message);
        CompletableFuture<String> second = CompletableFuture.supplyAsync(this::greetings);

        first.thenCombine(second, this::combinate)
                .thenApply(this::beautify)
                .thenAccept(this::consumeMessage);

    }

    String greetings() {
        return "good morning";
    }

    String combinate(String first, String second) {
        return String.format("%s -- %s", first, second);
    }

    String message() {
        return String.format("hey duke: %d", System.currentTimeMillis());
    }

    String beautify(String input) {
        return String.format("+ %s +", input);
    }

    void consumeMessage(String message) {
        System.out.println("message = " + message );
    }

    void finalAction() {
        System.out.println("Clean up!");
    }

}
