package com.dynamicdriller.velox.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {
    @TypeConverter
    fun toBitMap(bytes: ByteArray?): Bitmap? {
        if (bytes == null) {
            return null
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitMap(bmp: Bitmap?): ByteArray? {
        if (bmp == null) {
            return null
        }
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}
