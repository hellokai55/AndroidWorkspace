package com.example.variantlib1

import com.example.variantlib1_api.HttpClient

class LibraryApi {
    private var client: HttpClient? = null

    fun execute() {
        println("execute is called from LibraryApi...")
        if (client == null) {
            setupClientInternally()
        }
        println(client?.post())
    }

    fun setClient(client: HttpClient) {
        this.client = client
    }

    private fun setupClientInternally() {
        this.client = Class.forName("com.example.variantlib1.OkHttpClient")
            .getConstructor().newInstance() as HttpClient
    }
}
