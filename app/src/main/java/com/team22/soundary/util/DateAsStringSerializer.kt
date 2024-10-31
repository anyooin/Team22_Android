package com.team22.soundary.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date

object DateAsStringSerializer : KSerializer<Date> {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Date {
        val string = decoder.decodeString()
        return dateFormatter.parse(string)
    }

    override fun serialize(encoder: Encoder, value: Date) {
        val string = dateFormatter.format(value)
        encoder.encodeString(string)
    }
}