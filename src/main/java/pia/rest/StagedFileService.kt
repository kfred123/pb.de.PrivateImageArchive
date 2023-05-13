package pia.rest;

import kotlinx.dnq.query.toList
import mu.KotlinLogging
import pia.database.Database
import pia.database.model.archive.StagedFile
import pia.rest.contract.ObjectList
import pia.rest.contract.StagedFileApiContract
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("stagedFiles")
public class StagedFileService {
    val logger = KotlinLogging.logger { }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun query(): Response {
        var response: Response
        response = try {
            val imageApiContractObjectList = ObjectList<StagedFileApiContract>()
            Database.connection.transactional(true) {
                for (stagedFile in StagedFile.all().toList()) {
                    val contract = StagedFileApiContract.fromDb(stagedFile)
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

    @Path("deleteAllDebug")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun deleteAllDebug(): Response {
        Database.connection.transactional {
            for (video in StagedFile.all().toList()) {
                // ToDo delete file too
                video.delete()
            }
        }
        return Response.ok().build()
    }
}
