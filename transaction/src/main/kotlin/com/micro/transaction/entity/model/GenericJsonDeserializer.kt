package com.micro.transaction.entity.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer

class GenericJsonDeserializer<Any>(private val type: Class<Any>) : Deserializer<Any> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        // No configuration needed
    }

    override fun deserialize(topic: String?, data: ByteArray?): Any?{
        val mapper = ObjectMapper()
        var myObj:  Any? = null
        try {
            myObj = mapper.readValue(data, type)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return myObj
    }
    override fun close() {
        // No resources to close
    }
}


