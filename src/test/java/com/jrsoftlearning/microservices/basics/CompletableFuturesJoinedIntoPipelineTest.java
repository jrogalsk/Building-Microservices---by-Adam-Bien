package com.jrsoftlearning.microservices.basics;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CompletableFuturesJoinedIntoPipelineTest {

    @Test
    public void pipeline() {
        CompletableFuture.supplyAsync(this::message).
                thenApply(this::beautify)
                .thenAccept(this::consumeMessage)
                .thenRun(this::finalAction);
    }

    @Test
    public void combinePipelines() throws ExecutionException, InterruptedException {
        CompletableFuture<String> first = CompletableFuture
                .supplyAsync(this::message)
                .thenApplyAsync(this::beautify);
        CompletableFuture<String> second = CompletableFuture
                .supplyAsync(this::greetings)
                .thenApply(this::beautify);

        first.thenCombine(second, this::combinate)
                .thenAccept(this::consumeMessage)
                .get();
    }

    @Test
    public void composingPipelines() {
        CompletableFuture
                .supplyAsync(this::message)
                .thenCompose(this::compose)
                .thenAccept(this::consumeMessage);
    }

    CompletionStage<String> compose(String input) {
        return CompletableFuture
                .supplyAsync(() -> input)
                .thenApply(this::beautify);
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
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CompletableFuturesJoinedIntoPipelineTest.class.getName()).log(Level.SEVERE, "");
        }

        return String.format("+ %s +", input);
    }

    void consumeMessage(String message) {
        System.out.println("message = " + message);
    }

    void finalAction() {
        System.out.println("Clean up!");
    }

}
