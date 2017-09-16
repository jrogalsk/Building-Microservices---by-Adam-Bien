package com.jrsoft.async_jaxrs.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("async")
public class AsyncResource {

    @GET
    public void get(@Suspended AsyncResponse response) {
        response.resume(this.doSomeWork()); // <- work is no longer managed in HTTP thread pool but in other thread pool, which does not block http calls!
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
