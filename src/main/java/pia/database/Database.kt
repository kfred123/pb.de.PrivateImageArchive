package pia.database

import jetbrains.exodus.database.TransientEntityStore
import kotlinx.dnq.XdModel
import kotlinx.dnq.store.container.StaticStoreContainer
import kotlinx.dnq.store.container.StaticStoreContainer.init
import kotlinx.dnq.util.initMetaData
import pia.database.model.archive.Image
import pia.database.model.archive.Video
import pia.filesystem.MediaType
import java.io.File

object Database {
    val connection: TransientEntityStore

    init {

        XdModel.registerNodes(Image, Video)
        connection = StaticStoreContainer.init(
            File(System.getProperty("user.home"), "privateimagearchive"),
            "db"
        )
        initMetaData(XdModel.hierarchy, connection)
    }
}