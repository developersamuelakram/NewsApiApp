package com.example.newsapiapp.db

data class News(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)