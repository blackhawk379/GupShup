package com.example.chatapplication.Model

import android.content.IntentSender
import android.net.Uri
import retrofit2.http.Url

class Message {
    var message: String? = null
    var senderId: String? = null
    var isImage: Boolean? = null
    constructor(){}

    constructor(message: String, senderId: String, isImage: Boolean){
        this.message = message
        this.senderId = senderId
        this.isImage = isImage
    }
}