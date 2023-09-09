package pia.rest

import java.util.LinkedList

class CommaSeparatedOptionsParser(options : String?) {
    private val optionsSet : List<String>
    init {
        optionsSet = options?.split(",") ?: LinkedList<String>()
    }

    public fun isEnabled(option : String) : Boolean {
        return optionsSet.contains(option)
    }
}