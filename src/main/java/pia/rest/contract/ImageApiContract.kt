package pia.rest.contract

import java.util.*
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class ImageApiContract(val id : UUID) {
    var fileName : String = ""
}
