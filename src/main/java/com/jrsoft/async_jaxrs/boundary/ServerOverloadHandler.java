package com.jrsoft.async_jaxrs.boundary;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.concurrent.RejectedExecutionException;

@Provider // this annotaition instals handler on application server
public class ServerOverloadHandler implements ExceptionMapper<RejectedExecutionException>{
    @Override
    public Response toResponse(RejectedExecutionException exception) {
        return  Response
                .status(Response.Status.SERVICE_UNAVAILABLE)
                .header("overload-reason", exception.toString())
                .build();
    }
}
