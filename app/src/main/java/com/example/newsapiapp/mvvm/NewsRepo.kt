package com.example.newsapiapp.mvvm

import androidx.lifecycle.LiveData
import com.example.newsapiapp.db.SavedArticle
import com.example.newsapiapp.service.RetrofitInstance

class NewsRepo(val newsDao: NewsDao) {

    // for all saved news
    fun getAllSavedNews() : LiveData<List<SavedArticle>>{
        return newsDao.getAllNews()
    }


    fun getNewsByid() : LiveData<SavedArticle> {
        return newsDao.getNewsById()
    }


    // getting breaking news

    suspend fun getBreakingNews(code: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(code, pageNumber)


    // getting category news

    suspend fun getCategoryNews(code: String) = RetrofitInstance.api.getByCategory(code)


    // to delete ALl news

    fun deleteAll(){
        newsDao.delteAll()
    }


    suspend fun insertNews(savedArticle: SavedArticle) {

        newsDao.insertNews(savedArticle)
    }
}