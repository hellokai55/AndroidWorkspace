package com.example.variantlib1

import com.example.variantlib1_api.HttpClient
import okhttp3.OkHttp

class OkHttpClient : HttpClient {
    override fun post() {
        println("post() is called from OkHttpClient ${OkHttp.VERSION}.")
    }
}
