package pia.database

import jetbrains.exodus.database.TransientEntityStore
import kotlinx.dnq.XdModel
import kotlinx.dnq.store.container.StaticStoreContainer
import kotlinx.dnq.util.initMetaData
import pia.database.model.archive.Image
import pia.database.model.archive.StagedFile
import pia.database.model.archive.StagedTypeEntity
import pia.database.model.archive.Video
import pia.tools.Configuration
import java.io.File

object Database {
    val connection: TransientEntityStore

    init {

        XdModel.registerNodes(Image, Video, StagedFile, StagedTypeEntity)
        connection = StaticStoreContainer.init(
            File(Configuration.getPathToFileStorage(), "db"),
            "db"
        )
        initMetaData(XdModel.hierarchy, connection)
    }
}