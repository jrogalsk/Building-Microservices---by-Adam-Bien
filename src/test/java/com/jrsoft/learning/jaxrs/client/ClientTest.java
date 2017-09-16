package com.jrsoft.learning.jaxrs.client;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class ClientTest {

    private Client client;
    private WebTarget tut;
    private WebTarget processor;

    @Before
    public void init() {
        this.client = ClientBuilder.newClient();
        this.client.property(ClientProperties.CONNECT_TIMEOUT, 100);
        this.client.property(ClientProperties.READ_TIMEOUT, 500);
        this.tut = this.client.target("http://localhost:8080/suplier/resources/messages");
        this.processor = this.client.target("http://localhost:8080/suplier/resources/processors/beautification");
    }

    @Test
    public void fetchMessage() throws InterruptedException, java.util.concurrent.ExecutionException {
        final ExecutorService pool = Executors.newFixedThreadPool(5);
        Supplier<String>  messageSupplier = () -> this.tut.request().get(String.class);
        CompletableFuture
                .supplyAsync(messageSupplier, pool)
                .thenApply(this::process)
                .exceptionally(this::handle)
                .thenAccept(this::consume)
                .get();
    }

    private String handle(Throwable throwable) {
        return "Sorry we are overloaded";
    }

    String process(String input) {
        Response response =  this.processor.request().post(Entity.text(input));
        return response.readEntity(String.class);
    }

    private void consume(String message) {
        this.tut.request().post(Entity.text(message));
    }

}
