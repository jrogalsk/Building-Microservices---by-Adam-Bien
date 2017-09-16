package com.jrsoft.async_jaxrs.boundary;

import org.glassfish.jersey.client.ClientProperties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("async")
public class AsyncResource {

    @Resource
    ManagedExecutorService mes;

    private Client client;
    private WebTarget tut;
    private WebTarget processor;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient();
        this.client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        this.client.property(ClientProperties.READ_TIMEOUT, 5000);
        this.tut = this.client.target("http://localhost:8080/suplier/resources/messages");
        this.processor = this.client.target("http://localhost:8080/suplier/resources/processors/beautification");
    }

    @GET
    @Path("orchestration")
    public String fetchMessage() throws InterruptedException, java.util.concurrent.ExecutionException {
        Supplier<String> messageSupplier = () -> this.tut.request().get(String.class);
        CompletableFuture
                .supplyAsync(messageSupplier, mes)
                .thenApply(this::process)
                .exceptionally(this::handle)
                .thenAccept(this::consume)
                .get();

        return "+++";
    }

    private String handle(Throwable throwable) {
        return String.format("Sorry we are overloaded: %s", throwable.toString());
    }

    String process(String input) {
        Response response =  this.processor.request().post(Entity.text(input));
        return response.readEntity(String.class);
    }

    private void consume(String message) {
        this.tut.request().post(Entity.text(message));
    }

    @GET
    public void get(@Suspended AsyncResponse response) {
        CompletableFuture
                .supplyAsync(this::doSomeWork, mes)
                .thenAccept(response::resume);
    }

    String doSomeWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Logger.getLogger(AsyncResource.class.getName()).log(Level.SEVERE, null, e);
        }

        return String.format("+%d", System.currentTimeMillis());
    }
}
