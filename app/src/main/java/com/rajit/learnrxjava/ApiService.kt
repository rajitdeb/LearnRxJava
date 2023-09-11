package com.rajit.learnrxjava

import com.rajit.learnrxjava.model.Product
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface ApiService {

    // here, we're wrapping the products list with Observable to make the response observable
    @GET("/products")
    fun getProducts(): Observable<List<Product>>

}