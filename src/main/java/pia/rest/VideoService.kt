package pia.rest

import kotlinx.dnq.query.toList
import mu.KotlinLogging
import org.eclipse.jetty.util.StringUtil
import org.glassfish.jersey.media.multipart.FormDataParam
import pia.database.Database
import pia.database.model.archive.Video
import pia.exceptions.CreateHashException
import pia.filesystem.BufferedFileWithMetaData
import pia.logic.VideoReader
import pia.logic.VideoWriter
import pia.rest.contract.ErrorContract
import pia.rest.contract.ObjectList
import pia.rest.contract.SuccessContract
import pia.rest.contract.VideoApiContract
import pia.tools.CurrentRunningUploadCounter
import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.time.LocalDateTime
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("videos")
class VideoService {
    val logger = KotlinLogging.logger {  }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun queryVideos(
        @QueryParam("embed") embedDetails : String?
    ): Response {
        val videoApiContractObjectList: ObjectList<VideoApiContract> = ObjectList<VideoApiContract>()
        try {
            Database.connection.transactional {
                for (video in Video.all().toList()) {
                    val contract: VideoApiContract = VideoApiContract.fromDbAndEmbedInfos(
                        video,
                        CommaSeparatedOptionsParser(embedDetails)
                    )
                    videoApiContractObjectList.add(contract)
                }
            }
        } catch (e : java.lang.Exception) {
            logger.error("error reading videos", e)
        }
        return Response.ok().entity(videoApiContractObjectList).build()
    }

    // ToDo endpoint to add creationTime to video infos

    @Path("checkVideoExistanceByHash")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun checkVideoExistanceByHash(
        @QueryParam("hashType") hashType: String?,
        @QueryParam("hash") hash: String?
    ): Response {
        val response: Response
        var hashTypeInternal = hashType
        if (StringUtil.isEmpty(hashTypeInternal)) {
            hashTypeInternal = "SHA-256"
        }
        val reader = VideoReader()
        response = if (reader.findVideosBySHA256(hash!!).size > 0) {
            Response.status(Response.Status.OK).build()
        } else {
            Response.status(Response.Status.NO_CONTENT).build()
        }
        return response
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun addVideo(
        @FormDataParam("video") videoStream: InputStream?,
        @FormDataParam("fileName") fileName: String?,
        @FormDataParam("creationTimeStamp") creationTimeStamp: LocalDateTime?
    ): Response? {
        videoStream.use {
            CurrentRunningUploadCounter().use {
                logger.info("CurrentRunningUploads: " + CurrentRunningUploadCounter.currentRunningUploads)
                // ToDo SCHWERWIEGEND: UnknownBox{type=    } might have been truncated by file end. bytesRead=13743 contentSize=1751411818 (added catch already)
                var response: Response? = null
                try {
                    val bufferedFile: BufferedFileWithMetaData =
                        BufferedFileWithMetaData.Companion.videoFromInputStream(videoStream!!, fileName!!)
                    val writer = VideoWriter()
                    writer.addVideo(bufferedFile, fileName)
                } catch (e: Throwable) {
                    logger.error("error adding video", e);
                    response = Response.serverError().entity(ErrorContract(e)).build()
                }
                if (response == null) {
                    response = Response.created(URI.create("")).build()
                }
                return response
            }
        }
    }

    @Path("{videoId}/getFile")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getVideoFile(@PathParam("videoId") videoId: String?): Response {
        val videoUuid = UUID.fromString(videoId)
        val videoReader = VideoReader()
        val fileStream: Optional<InputStream> = videoReader.getVideoFileByVideoIdFromDisk(videoUuid)
        val response: Response
        response = if (fileStream.isPresent) {
            Response.ok().entity(fileStream.get()).build()
        } else {
            Response.noContent().build()
        }
        return response
    }

    @Path("{videoId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteVideo(@PathParam("videoId") videoId: String?): Response {
        val response: Response
        val videoUuid = UUID.fromString(videoId)
        val videoReader = VideoReader()
        val video: Optional<Video> = videoReader.findVideoById(videoUuid)
        response = if (video.isPresent()) {
            val videoWriter = VideoWriter()
            if (videoWriter.deleteVideo(video.get())) {
                Response.ok().entity(SuccessContract("successfully deleted")).build()
            } else {
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ErrorContract("error deleting the video")).build()
            }
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
        return response
    }

    @Path("deleteAllDebug")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteAllDebug(): Response {
        val writer = VideoWriter()
        Database.connection.transactional {
            for (video in Video.all().toList()) {
                writer.deleteVideo(video)
            }
        }
        return Response.ok().build()
    }
}