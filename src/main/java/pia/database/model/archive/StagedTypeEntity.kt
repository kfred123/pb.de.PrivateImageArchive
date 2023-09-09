package pia.database.model.archive

import jetbrains.exodus.entitystore.Entity
import kotlinx.dnq.*

class StagedTypeEntity(entity: Entity) : XdEnumEntity(entity) {
    companion object : XdEnumEntityType<StagedTypeEntity>() {
        val Image by enumField { presentation = "image" }
        val Video by enumField { presentation = "video" }

        fun get(type : StagedType) : StagedTypeEntity {
            return when(type) {
                StagedType.Image -> Image
                StagedType.Video -> Video
            }
        }
    }

    var presentation by xdRequiredStringProp()
        private set
}