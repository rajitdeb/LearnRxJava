package com.rajit.learnrxjava

import com.rajit.learnrxjava.model.MultiMeme
import com.rajit.learnrxjava.model.Product
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    // here, we're wrapping the products list with Observable to make the response observable
    @GET("/products")
    fun getProducts(): Observable<List<Product>>

    @GET("/gimme/{subReddit}/25")
    fun getSubredditMemes(
        @Path("subReddit") subReddit: String
    ): Observable<MultiMeme>

}