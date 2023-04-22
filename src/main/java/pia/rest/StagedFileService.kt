package pia.rest;

import kotlinx.dnq.query.query
import kotlinx.dnq.query.toList
import mu.KotlinLogging
import pia.database.Database
import pia.database.Database.connection
import pia.database.model.archive.StagedFile
import pia.database.model.archive.Video
import pia.filesystem.FileSystemHelper
import pia.logic.FileStager
import pia.logic.VideoWriter
import pia.rest.contract.ImageApiContract
import pia.rest.contract.ObjectList
import pia.rest.contract.StagedFileApiContract
import javax.ws.rs.*
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("stagedFiles")
public class StagedFileService {
    val logger = KotlinLogging.logger {  }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun query(): Response {
        var response :Response
        response = try {
            val imageApiContractObjectList = ObjectList<StagedFileApiContract>()
            connection.transactional(true) {

                for (stagedFile in StagedFile.all().toList()) {
                    val contract = StagedFileApiContract.fromDb(stagedFile)
                    imageApiContractObjectList.add(contract)
                }
            }
            Response.ok().entity(imageApiContractObjectList).build()
        } catch (e : java.lang.Exception) {
            logger.error(e) {  }
            Response.serverError().build()
        }
        return response
    }

    @Path("deleteAllDebug")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteAllDebug(): Response {
        connection.transactional {
            for (video in StagedFile.all().toList()) {
                // ToDo delete file too
                video.delete()
            }
        }
        return Response.ok().build()
    }
}
