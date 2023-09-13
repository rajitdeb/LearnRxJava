package com.rajit.learnrxjava

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.TextViewTextChangeEvent
import com.jakewharton.rxbinding4.widget.textChangeEvents
import com.rajit.learnrxjava.databinding.ActivityMainBinding
import com.rajit.learnrxjava.model.MultiMeme
import com.rajit.learnrxjava.model.Product
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var count = 0

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        simpleObserver()

        createObservable()

        binding.showDataBtn
            .clicks()
            .throttleFirst(
                1500,
                TimeUnit.MILLISECONDS
            ) // only registers another click after 1500ms after a click
            .subscribe {
                binding.debugTxt.text = "Button Clicked ${count++}"
            }

//        implementNetworkCall()
        implementMemeSearchNetworkCall()
    }

    // This function demonstrates the usage of debounce to search memes based on text change events
    // after a certain period of time {HERE, [500 MILLISECONDS]}
    // and displaying it on the screen
    @SuppressLint("CheckResult")
    private fun implementMemeSearchNetworkCall() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://meme-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // this adapter converts the response into observable
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        binding.searchEdt.editText!!.textChangeEvents()
            .debounce(500, TimeUnit.MILLISECONDS) // used debouncing to only search after certain time
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TextViewTextChangeEvent> {
                override fun onSubscribe(d: Disposable) {
                    Log.i(TAG, "onSubscribe Called")
                }

                override fun onError(e: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onComplete() {
                    TODO("Not yet implemented")
                }

                override fun onNext(t: TextViewTextChangeEvent) {
                    if (t.text.isNotEmpty()) {
                        Toast.makeText(this@MainActivity, "${t.text}", Toast.LENGTH_SHORT).show()
                        searchMemes(apiService, t.text)
                    }
                }
            })

    }

    // This function uses the search query to search for memes
    @SuppressLint("CheckResult")
    private fun searchMemes(apiService: ApiService, text: CharSequence) {
        apiService
            .getSubredditMemes(text.toString())
            .subscribeOn(Schedulers.io()) // Do all the network operations on IO Thread [This method works UPSTREAM; meaning it applies to all the methods above it]
            .observeOn(AndroidSchedulers.mainThread()) // Do all the binding and observe on Main Thread [This method works DOWNSTREAM; meaning it applies to all the methods below it]
            .subscribe(object : Observer<MultiMeme> {
                override fun onSubscribe(d: Disposable) {
                    Log.i(TAG, "onSubscribe Called")
                    // every time a new search query is passed, clear the previous results
                    // from the textview
                    binding.debugTxt.text = ""
                }

                override fun onError(e: Throwable) {
                    Log.i(TAG, "onError Called")
                }

                override fun onComplete() {
                    Log.i(TAG, "onComplete Called")
                }

                override fun onNext(t: MultiMeme) {
                    Log.i(TAG, "onNext Called")
                    var itemCount = 1
                    for (item in t.memes) {
                        // show the memes in the textview
                        binding.debugTxt.append("${itemCount++}. ${item.title}\n")
                    }
                }
            })
    }

    // This function demonstrates how to do a network call, create an observable and observe it on different threads
    // Threading is also take care of in this function
    @SuppressLint("CheckResult")
    private fun implementNetworkCall() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // this adapter converts the response into observable
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService
            .getProducts()
            .subscribeOn(Schedulers.io()) // Do all the network operations on IO Thread [This method works UPSTREAM; meaning it applies to all the methods above it]
            .observeOn(AndroidSchedulers.mainThread()) // Do all the binding and observe on Main Thread [This method works DOWNSTREAM; meaning it applies to all the methods below it]
            .subscribe {
                var itemCount = 1
                for (item in it) {
                    binding.debugTxt.append("${itemCount++}. ${item.title}\n")
                }
            }

    }

    // This function demonstrates how to create an observable with the create method
    // and handle exceptions and data streams manually (based on some logic)
    private fun createObservable() {
        val observable = Observable.create<String> { emitter ->
            emitter.onNext("One")
            emitter.onError(IllegalArgumentException("Illegal argument passed"))
            emitter.onNext("Two")
            emitter.onComplete()
        }

        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.i("$TAG Observer2", "MainActivity: onSubscribe called")
            }

            override fun onError(e: Throwable) {
                Log.e("$TAG Observer2", "MainActivity: onError called - ${e.message}")
                binding.debugTxt.text = "onError called - ${e.message}"
            }

            override fun onComplete() {
                Log.i("$TAG Observer2", "MainActivity: onComplete called")
            }

            override fun onNext(t: String) {
                Log.i("$TAG Observer2", "MainActivity: onNext called - $t")
            }

        })


    }

    // This function demonstrate how to observe an observable that emits some data
    private fun simpleObserver() {

        val list = listOf<String>("A", "B", "C")
        val observable = Observable.fromIterable(list)

        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                Log.i("$TAG Observer", "MainActivity: onSubscribe called")
            }

            override fun onError(e: Throwable) {
                Log.i("$TAG Observer", "MainActivity: onError called - ${e.message}")
            }

            override fun onComplete() {
                Log.i("$TAG Observer", "MainActivity: onComplete called")
            }

            override fun onNext(t: String) {
                Log.i("$TAG Observer", "MainActivity: onNext called - $t")
            }

        })

    }


}