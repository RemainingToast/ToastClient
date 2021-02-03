package me.remainingtoast.toastclient.api.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import java.awt.Color

object CustomSerializers {
    object ColorSerializer : KSerializer<Color> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("color") {
                element<Int>("red")
                element<Int>("green")
                element<Int>("blue")
                element<Int>("alpha")
            }

        override fun serialize(encoder: Encoder, value: Color) =
            encoder.encodeStructure(descriptor) {
                encodeIntElement(descriptor, 0, value.red)
                encodeIntElement(descriptor, 1, value.green)
                encodeIntElement(descriptor, 2, value.blue)
                encodeIntElement(descriptor, 3, value.alpha)
            }

        override fun deserialize(decoder: Decoder): Color =
            decoder.decodeStructure(descriptor) {
                var r = -1
                var g = -1
                var b = -1
                var a = -1
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> r = decodeIntElement(descriptor, 0)
                        1 -> g = decodeIntElement(descriptor, 1)
                        2 -> b = decodeIntElement(descriptor, 2)
                        3 -> a = decodeIntElement(descriptor, 3)
                        CompositeDecoder.DECODE_DONE -> break
                        else -> error("Unexpected index: $index")
                    }
                }
                require(a in 0..255 && g in 0..255 && b in 0..255 && a in 0..255)
                Color(r, g, b, a)
            }
    }
}