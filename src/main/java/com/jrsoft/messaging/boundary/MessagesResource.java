package com.jrsoft.messaging.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("messages")
public class MessagesResource {

    @GET
    public String message() {
        return String.format("Hey Duke %d", System.currentTimeMillis());
    }

}
