package pia.rest;

import kotlin.text.StringsKt;
import org.eclipse.jetty.util.StringUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import pia.database.Database;
import pia.database.DatabaseQuery;
import pia.database.model.archive.Image;
import pia.exceptions.CreateHashException;
import pia.filesystem.BufferedFile;
import pia.logic.ImageReader;
import pia.logic.ImageWriter;
import pia.rest.contract.ErrorContract;
import pia.rest.contract.ImageApiContract;
import pia.rest.contract.ObjectList;
import pia.rest.contract.SuccessContract;
import pia.tools.FileHash;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Path("images")
public class ImageService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryImages() {
        ObjectList<ImageApiContract> imageApiContractObjectList = new ObjectList<>();
        DatabaseQuery<Image> query = Database.getConnection().query(Image.class);
        for(Image image : query.getAll()) {
            ImageApiContract contract = ImageApiContract.Companion.fromDb(image);
            imageApiContractObjectList.add(contract);
        }
        return Response.ok().entity(imageApiContractObjectList).build();
    }

    @Path("checkImageExistanceByHash")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkImageExistanceByHash(@QueryParam("hashType") String hashType,
                                              @QueryParam("hash") String hash) {
        Response response;
        String hashTypeInternal = hashType;
        if(StringUtil.isEmpty(hashTypeInternal)) {
            hashTypeInternal = "SHA-256";
        }
        ImageReader reader = new ImageReader();
        if(reader.findImagesBySHA256(hash).size() > 0) {
            response = Response.status(Response.Status.OK).build();
        } else {
            response = Response.status(Response.Status.NO_CONTENT).build();
        }
        return response;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addImage(@FormDataParam("image")InputStream imageStream,
                             @FormDataParam("fileName")String fileName) {
        Response response = null;
        try {
            ImageReader reader = new ImageReader();
            BufferedFile bufferedFile = new BufferedFile(imageStream.readAllBytes());
            Optional<String> hash = FileHash.createHash(bufferedFile);
            if(hash.isPresent()) {
                if(reader.findImagesBySHA256(hash.get()).size() == 0) {
                    ImageWriter writer = new ImageWriter();
                    writer.addImage(bufferedFile, fileName);
                } else {
                    response = Response.status(Response.Status.FOUND).entity(new ErrorContract("imagefile already uploaded")).build();
                }
            } else {
                response = Response.serverError().entity(new ErrorContract("could not create hash for image")).build();
            }
        } catch (IOException e) {
            response = Response.serverError().entity(new ErrorContract(e)).build();
        } catch (CreateHashException e) {
            response = Response.serverError().entity(new ErrorContract(e)).build();
        } catch (Throwable e) {
            response = Response.serverError().entity(new ErrorContract(e)).build();
        }
        if(response == null) {
            response = Response.accepted().build();
        }
        return response;
    }

    @Path("{imageId}/getFile")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getImageFile(@PathParam("imageId")String imageId) {
        UUID imaageUUID = UUID.fromString(imageId);
        ImageReader imageReader = new ImageReader();
        Optional<InputStream> fileStream = imageReader.getImageFileByImageIdFromDisk(imaageUUID);

        Response response;
        if(fileStream.isPresent()) {
            response = Response.ok().entity(fileStream.get()).build();
        } else {
            response = Response.noContent().build();
        }
        return response;
    }

    @Path("{imageId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteImage(@PathParam("imageId")String imageId) {
        Response response;
        UUID imageUUID = UUID.fromString(imageId);
        ImageReader imageReader = new ImageReader();
        Optional<Image> image = imageReader.findImageById(imageUUID);
        if(image.isPresent()) {
            ImageWriter imageWriter = new ImageWriter();
            if(imageWriter.deleteImage(image.get())) {
                response = Response.ok().entity(new SuccessContract("successfully deleted")).build();
            } else {
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorContract("error deleting the image")).build();
            }
        } else {
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    @Path("deleteAllDebug")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllDebug() {
        ImageWriter writer = new ImageWriter();
        DatabaseQuery<Image> query = Database.getConnection().query(Image.class);
        for(Image image : query.getAll()) {
            writer.deleteImage(image);
        }
        return Response.ok().build();
    }
}
