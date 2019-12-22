package pia.rest.contract

import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement
class SuccessContract(val successMessage: String) {
}