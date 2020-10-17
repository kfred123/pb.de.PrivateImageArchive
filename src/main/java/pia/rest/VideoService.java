package pia.rest;

import org.eclipse.jetty.util.StringUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import pia.database.Database;
import pia.database.DatabaseQuery;
import pia.database.model.archive.Video;
import pia.exceptions.CreateHashException;
import pia.filesystem.BufferedFileWithMetaData;
import pia.logic.VideoReader;
import pia.logic.VideoWriter;
import pia.rest.contract.ErrorContract;
import pia.rest.contract.ObjectList;
import pia.rest.contract.SuccessContract;
import pia.rest.contract.VideoApiContract;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Path("videos")
public class VideoService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryVideos() {
        ObjectList<VideoApiContract> videoApiContractObjectList = new ObjectList<>();
        DatabaseQuery<Video> query = Database.getConnection().query(Video.class);
        for(Video video : query.getAll()) {
            VideoApiContract contract = VideoApiContract.Companion.fromDb(video);
            videoApiContractObjectList.add(contract);
        }
        return Response.ok().entity(videoApiContractObjectList).build();
    }

    @Path("checkVideoExistanceByHash")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkVideoExistanceByHash(@QueryParam("hashType") String hashType,
                                              @QueryParam("hash") String hash) {
        Response response;
        String hashTypeInternal = hashType;
        if(StringUtil.isEmpty(hashTypeInternal)) {
            hashTypeInternal = "SHA-256";
        }
        VideoReader reader = new VideoReader();
        if(reader.findVideosBySHA256(hash).size() > 0) {
            response = Response.status(Response.Status.OK).build();
        } else {
            response = Response.status(Response.Status.NO_CONTENT).build();
        }
        return response;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVideo(@FormDataParam("video")InputStream videoStream,
                             @FormDataParam("fileName")String fileName,
                             @FormDataParam("creationTimeStamp")LocalDateTime creationTimeStamp) {
        Response response = null;
        try {
            BufferedFileWithMetaData bufferedFile = BufferedFileWithMetaData.Companion.videoFromInputStream(videoStream);
            VideoWriter writer = new VideoWriter();
            writer.addVideo(bufferedFile, fileName);
        } catch (IOException e) {
            response = Response.serverError().entity(new ErrorContract(e)).build();
        } catch (CreateHashException e) {
            response = Response.serverError().entity(new ErrorContract(e)).build();
        } catch (Throwable e) {
            response = Response.serverError().entity(new ErrorContract(e)).build();
        }
        if(response == null) {
            response = Response.created(URI.create("")).build();
        }
        return response;
    }

    @Path("{videoId}/getFile")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getVideoFile(@PathParam("videoId")String videoId) {
        UUID videoUuid = UUID.fromString(videoId);
        VideoReader videoReader = new VideoReader();
        Optional<InputStream> fileStream = videoReader.getVideoFileByVideoIdFromDisk(videoUuid);

        Response response;
        if(fileStream.isPresent()) {
            response = Response.ok().entity(fileStream.get()).build();
        } else {
            response = Response.noContent().build();
        }
        return response;
    }

    @Path("{videoId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteVideo(@PathParam("videoId")String videoId) {
        Response response;
        UUID videoUuid = UUID.fromString(videoId);
        VideoReader videoReader = new VideoReader();
        Optional<Video> video = videoReader.findVideoById(videoUuid);
        if(video.isPresent()) {
            VideoWriter videoWriter = new VideoWriter();
            if(videoWriter.deleteVideo(video.get())) {
                response = Response.ok().entity(new SuccessContract("successfully deleted")).build();
            } else {
                response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorContract("error deleting the video")).build();
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
        VideoWriter writer = new VideoWriter();
        DatabaseQuery<Video> query = Database.getConnection().query(Video.class);
        for(Video video : query.getAll()) {
            writer.deleteVideo(video);
        }
        return Response.ok().build();
    }
}
