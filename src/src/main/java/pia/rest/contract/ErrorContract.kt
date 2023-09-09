package pia.rest.contract

class ErrorContract(var error : String) {
    constructor(exception : Throwable) : this(exception.message.orEmpty())  {
    }
}