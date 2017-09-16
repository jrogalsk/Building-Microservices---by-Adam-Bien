package com.jrsoft.async_jaxrs.boundary;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("async")
public class AsyncResource {

    @Resource
    ManagedExecutorService mes;

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
