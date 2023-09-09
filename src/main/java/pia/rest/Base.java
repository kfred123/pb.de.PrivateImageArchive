package pia.rest;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class Base {
    @GET()
    public Response isAvailable() {
        return Response.ok().build();
    }
}
