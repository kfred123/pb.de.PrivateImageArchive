package pia.rest;

import org.mongodb.morphia.query.Query;
import pia.database.Database;
import pia.database.model.archive.Image;
import pia.exceptions.CreateHashException;
import pia.logic.ImageWriter;
import pia.rest.contract.ImageApiContract;
import pia.rest.contract.ObjectList;
import pia.tools.FileHash;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.util.Optional;

@Path("images")
public class ImageService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryImages() {
        ObjectList<ImageApiContract> imageApiContractObjectList = new ObjectList<>();
        Query<Image> query = Database.getConnection().createQuery(Image.class);
        for(Image image : query) {
            ImageApiContract contract = new ImageApiContract(image.getId());
            contract.setFileName(image.getOriginalFileName());
            imageApiContractObjectList.add(contract);
        }
        return Response.ok().entity(imageApiContractObjectList).build();
    }

    @Path("upload")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addImage(@FormParam("image")InputStream imageStream,
                             @FormParam("fileName")String fileName) {
        Response response = null;
        Image image = new Image();
        try {
            ImageWriter writer = new ImageWriter();
            writer.addImage(imageStream, fileName);
        } catch (IOException e) {
            response = Response.serverError().entity(e).build();
        } catch (CreateHashException e) {
            response = Response.serverError().entity(e).build();
        } catch (Throwable e) {
            response = Response.serverError().entity(e).build();
        }
        if(response == null) {
            response = Response.accepted().build();
        }
        return response;
    }
}
