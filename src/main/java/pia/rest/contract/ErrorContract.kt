package pia.rest.contract

import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class ErrorContract(var error : String) {
    constructor(exception : Throwable) : this(exception.message.orEmpty())  {
    }
}