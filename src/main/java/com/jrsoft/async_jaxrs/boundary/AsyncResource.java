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
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("async")
public class AsyncResource {

    @Resource
    ManagedExecutorService managedThreadPool;

    private Client client;
    private WebTarget messagesMicroService;
    private WebTarget processorMicroService;

    @PostConstruct
    public void init() {
        this.client = ClientBuilder.newClient();
        this.client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        this.client.property(ClientProperties.READ_TIMEOUT, 5000);
        this.messagesMicroService = this.client.target("http://localhost:8080/suplier/resources/messages");
        this.processorMicroService = this.client.target("http://localhost:8080/suplier/resources/processors/beautification");
    }

    @GET
    @Path("orchestration")
    public void fetchMessage(@Suspended AsyncResponse response) throws InterruptedException, ExecutionException {
        CompletableFuture
                .supplyAsync(this::fetchMessageFromFirsMiscroService, managedThreadPool)
                .thenApply(this::processTheMessageInAnotherMicroservice)
                .exceptionally(this::handleExceptions)
                .thenApply(this::pushTheResultsBackToFirstMicroservice)
                .thenAccept(response::resume);

    }

    private String fetchMessageFromFirsMiscroService() {
        return this.messagesMicroService.request().get(String.class);
    }

    private String handleExceptions(Throwable throwable) {
        return String.format("Sorry we are overloaded: %s", throwable.toString());
    }

    String processTheMessageInAnotherMicroservice(String input) {
        Response response =  this.processorMicroService.request().post(Entity.text(input));
        return response.readEntity(String.class);
    }

    private String pushTheResultsBackToFirstMicroservice(String message) {
        this.messagesMicroService.request().post(Entity.text(message));
        return message;
    }

    @GET
    public void get(@Suspended AsyncResponse response) {
        CompletableFuture
                .supplyAsync(this::doSomeWork, managedThreadPool)
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
