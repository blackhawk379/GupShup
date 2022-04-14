package com.example.chatapplication.Model

class GroupMessages {
    var message: String? = null
    var senderId: String? = null
    var senderName: String? = "sender"
    constructor(){}

    constructor(message: String, senderId: String, senderName: String){
        this.message = message
        this.senderId = senderId
        this.senderName = senderName
    }
}