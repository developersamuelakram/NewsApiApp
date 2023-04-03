package com.example.newsapiapp.db

import androidx.room.TypeConverter

class ClassConvertors {
    // room wont be able to convert
    // custom classes that we are using as a data types
    // room can only convert primitive

    @TypeConverter
    fun fromSource(source: Source): String{
        return source.name!!
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}