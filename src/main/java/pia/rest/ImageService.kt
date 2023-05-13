package pia.rest

import jetbrains.exodus.query.GetAll
import jetbrains.exodus.query.NodeBase
import kotlinx.dnq.query.eq
import kotlinx.dnq.query.query
import kotlinx.dnq.query.toList
import mu.KotlinLogging
import org.eclipse.jetty.util.StringUtil
import org.glassfish.jersey.media.multipart.FormDataParam
import pia.database.Database
import pia.database.model.archive.Image
import pia.database.model.archive.StagedType
import pia.exceptions.InternalException
import pia.logic.FileStager
import pia.logic.ImageReader
import pia.logic.ImageWriter
import pia.logic.WorkBalancer
import pia.rest.contract.ErrorContract
import pia.rest.contract.ImageApiContract
import pia.rest.contract.ImageApiContract.Companion.fromDb
import pia.rest.contract.ObjectList
import pia.rest.contract.SuccessContract
import pia.tools.toEntityId
import java.io.InputStream
import java.net.URI
import java.time.LocalDateTime
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("images")
class ImageService {
    val logger = KotlinLogging.logger { }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun queryImages(@QueryParam("missingFilePath") missingFilePath: Boolean): Response {
        var response: Response
        response = try {
            val imageApiContractObjectList = ObjectList<ImageApiContract>()
            Database.connection.transactional(true) {

                for (image in Image.query(applyFilter(missingFilePath)).toList()) {
                    val contract = fromDb(image)
                    imageApiContractObjectList.add(contract)
                }
            }
            Response.ok().entity(imageApiContractObjectList).build()
        } catch (e: java.lang.Exception) {
            logger.error(e) { }
            Response.serverError().build()
        }
        return response
    }

    private fun applyFilter(missingFilePath: Boolean): NodeBase {
        var nodeBase: NodeBase? = null
        if (missingFilePath) {
            nodeBase = Image::pathToFileOnDisk eq ""
        }
        if (nodeBase == null) {
            nodeBase = GetAll()
        }
        return nodeBase
    }

    @Path("checkImageExistanceByHash")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun checkImageExistanceByHash(
        @QueryParam("hashType") hashType: String?,
        @QueryParam("hash") hash: String?
    ): Response {
        var response: Response
        try {
            var hashTypeInternal = hashType
            if (StringUtil.isEmpty(hashTypeInternal)) {
                hashTypeInternal = "SHA-256"
            }
            val reader = ImageReader()
            response = if (reader.findImagesBySHA256(hash!!).size > 0) {
                Response.status(Response.Status.OK).build()
            } else {
                Response.status(Response.Status.NO_CONTENT).build()
            }
        } catch (e: java.lang.Exception) {
            logger.error(e) { }
            response = Response.serverError().build()
        }
        return response
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun addImage(
        @FormDataParam("image") imageStream: InputStream,
        @FormDataParam("fileName") fileName: String?,
        @FormDataParam("creationTimeStamp") creationTimeStamp: LocalDateTime?
    ): Response {
        //logger.info("CurrentRunningUploads: " + CurrentRunningUploadCounter.currentRunningUploads)
        WorkBalancer.startStagingFile()
        val response: Response = try {
            imageStream.use {
                FileStager().stageFile(imageStream, fileName.orEmpty(), StagedType.Image)
                Response.created(URI.create("")).build()
            }
        } catch (e: Throwable) {
            logger.error(String.format("error adding image %s", fileName), e);
            Response.serverError().entity(ErrorContract(e)).build()
        }
        WorkBalancer.endStagingFile()
        return response
    }

    @Path("{imageId}/getFile")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getImageFile(@PathParam("imageId") imageId: String): Response {
        val response: Response = try {
            val imageReader = ImageReader()
            val fileStream = imageReader.getImageFileByImageIdFromDisk(imageId.toEntityId())
            if (fileStream.isPresent) {
                Response.ok().entity(fileStream.get()).build()
            } else {
                Response.noContent().build()
            }
        } catch (e: Exception) {
            logger.error(e) { }
            Response.serverError().build()
        }
        return response
    }

    @Path("{imageId}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteImage(@PathParam("imageId") imageId: String): Response {
        var response: Response
        try {
            val imageUUID = UUID.fromString(imageId)
            val imageReader = ImageReader()
            val image = imageReader.findImageById(imageId.toEntityId())
            response = if (image.isPresent) {
                val imageWriter = ImageWriter()
                if (imageWriter.deleteImage(image.get(), false)) {
                    Response.ok().entity(SuccessContract("successfully deleted")).build()
                } else {
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(ErrorContract("error deleting the image")).build()
                }
            } else {
                Response.status(Response.Status.NOT_FOUND).build()
            }
        } catch (e: java.lang.Exception) {
            logger.error(e) { }
            response = Response.serverError().build()
        }
        return response
    }

    @Path("deleteAllDebug")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteAllDebug(): Response {
        var response: Response
        try {
            val writer = ImageWriter()
            Database.connection.transactional {
                for (image in Image.all().toList()) {
                    if (!writer.deleteImage(image, true)) {
                        throw InternalException("unable to delete image ${image.pathToFileOnDisk}")
                    }
                }
                it.commit()
            }
            response = Response.ok().build()
        } catch (e: java.lang.Exception) {
            logger.error(e) {}
            response = Response.serverError().build()
        }
        return response
    }
}