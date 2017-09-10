package com.jrsoft.learning.microservices.threading.basics;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.System.out;

public class ExceptionsInCompletableFuturePipelines {

    @Test
    public void handle() throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(this::exceptional)
                .exceptionally(this::handleException)
                .thenAccept(this::consume)
                .get();
    }


    private String exceptional() {
        throw new IllegalStateException("happens");
    }


    String handleException(Throwable t) {
        return t.toString();
    }

    void consume(String message) {
        out.println("messaeg = " + message);
    }



}
