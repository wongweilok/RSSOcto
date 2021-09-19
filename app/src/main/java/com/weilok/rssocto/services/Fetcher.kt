package com.weilok.rssocto.services

import com.weilok.rssocto.data.remote.AtomFeed
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface Fetcher {
    @GET
    suspend fun getAtomFeed(@Url url: String): AtomFeed

    companion object {
        private const val BASE_URL = "https://blog.jetbrains.com/kotlin/"

        fun getInstance(): Fetcher {
            return Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(Fetcher::class.java)
        }
    }
}