package com.example.chatapplication

class message {

    var message : String? = null
    var senderID :String? = null
    var pdfName: String? = null
    var pdfUrl: String? = null

    constructor(){}

    constructor(message: String?,senderID : String?,pdfUrl : String?,pdfName: String?){
        this.message = message
        this.senderID = senderID
    }
    constructor(senderID: String?,pdfUrl : String?,pdfName: String?){
        this.senderID = senderID
        this.pdfUrl = pdfUrl
        this.pdfName = pdfName
    }
}