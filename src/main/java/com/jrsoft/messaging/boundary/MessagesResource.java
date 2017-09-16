package com.jrsoft.messaging.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("messages")
public class MessagesResource {

    @GET
    public String message() throws InterruptedException {
        Thread.sleep(1000);
        return String.format("Hey Duke %d", System.currentTimeMillis());
    }

    @POST
    public void message(String message) {
        System.out.printf("message = %s%n", message);
    }

}
