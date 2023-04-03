package com.example.newsapiapp.wrapper

sealed class Resource<T> (


    val data: T? = null,
    val message: String? = null)

    {
        class Success<T>(data: T) : Resource<T>(data)
        class Error<T>(message: String, data: T?= null) : Resource<T>(data, message)// if there is an error and body to it
        class Loading<T>: Resource<T>()

        // successful and error response
        // also helps with loading state
    }