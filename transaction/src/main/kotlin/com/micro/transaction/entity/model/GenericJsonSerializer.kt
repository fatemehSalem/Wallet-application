package com.micro.transaction.entity.model

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class GenericJsonSerializer<T> : Serializer<T> {

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        // No configuration needed
    }

    override fun serialize(topic: String?, data: T?): ByteArray? {
        var retVal: ByteArray? = null
        val objectMapper = ObjectMapper()
        try {
            retVal = objectMapper.writeValueAsString(data).toByteArray()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return retVal
    }


    override fun close() {
        // No resources to close
    }
}