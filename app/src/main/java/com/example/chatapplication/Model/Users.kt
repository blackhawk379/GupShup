package com.example.chatapplication.Model

import android.widget.Toast

class Users {
    private var Cover: String = ""
    private var EmailId: String = ""
    private var FaceBook: String = ""
    private var Instagram: String = ""
    private var Profile: String = ""
    private var Search: String = ""
    private var Status: String = ""
    private var Username: String = ""
    private var Website: String = ""
    private var Groups: ArrayList<String> = ArrayList()
    private var Friends: ArrayList<String> = ArrayList()
    private var Requests: ArrayList<String> = ArrayList()
    private var Requested: ArrayList<String> = ArrayList()
    private var AttachLinks: ArrayList<String> = ArrayList()
    private var AttachIcons: ArrayList<Int> = ArrayList()
    var uid: String = ""

    constructor()
    constructor(Cover: String, EmailId: String, FaceBook: String, Instagram: String, Profile: String, Search: String, Status: String, Username: String, Website: String, uid: String, Groups: ArrayList<String>, Friends: ArrayList<String>, Requests: ArrayList<String>, Requested: ArrayList<String>, AttachLinks: ArrayList<String> = ArrayList(), AttachIcons: ArrayList<Int> = ArrayList()) {
        this.Cover = Cover
        this.EmailId = EmailId
        this.FaceBook = FaceBook
        this.Instagram = Instagram
        this.Profile = Profile
        this.Search = Search
        this.Status = Status
        this.Username = Username
        this.Website = Website
        this.uid = uid
        this.Groups = Groups
        this.Friends = Friends
        this.Requests = Requests
        this.Requested = Requested
        this.AttachLinks = AttachLinks
        this.AttachIcons = AttachIcons
    }

    fun getWebsite(): String? {
        return Website
    }

    fun setWebsite(Website: String) {
        this.Website = Website
    }
    fun getCover(): String? {
        return Cover
    }

    fun setCover(Cover: String) {
        this.Cover = Cover
    }
    fun getProfile(): String? {
        return Profile
    }

    fun setProfile(Profile: String) {
        this.Profile = Profile
    }
    fun getFaceBook(): String? {
        return FaceBook
    }

    fun setFaceBook(Facebook: String) {
        this.FaceBook = FaceBook
    }
    fun getInstagram(): String? {
        return Instagram
    }

    fun setInstagram(Instagram: String) {
        println("-------${Instagram}-------")
        this.Instagram = Instagram
    }
    fun getSearch(): String? {
        return Search
    }

    fun setSearch(Search: String) {
        this.Search = Search
    }
    fun getStatus(): String? {
        return Status
    }

    fun setStatus(Status: String) {
        this.Status = Status
    }

    fun getEmailId(): String? {
        return EmailId
    }

    fun setEmailId(EmailId: String) {
        this.EmailId = EmailId
    }

    fun getUsername(): String? {
        return Username
    }

    fun getGroups(): ArrayList<String>{
        return Groups
    }
    fun getGroupn(): Int {
        return Groups.size
    }
    fun getFriends(): ArrayList<String>{
        return Friends
    }
    fun getFriendn(): Int {
        return Friends.size
    }
    fun getRequests(): ArrayList<String>{
        return Requests
    }
    fun getRequested(): ArrayList<String>{
        return Requested
    }
    fun getRequestn(): Int {
        return Requests.size
    }

    fun setUsername(Username: String) {
        println("-------${Username}-------")
        this.Username = Username
    }


    fun getAttachLinks(): ArrayList<String>{
        return AttachLinks
    }

    fun setAttachLinks(Attachment: ArrayList<String>){
        this.AttachLinks = AttachLinks
    }

    fun getAttachIcons(): ArrayList<Int>{
        return AttachIcons
    }

    fun setAttachIcons(AttachIcons: ArrayList<Int>){
        this.AttachIcons = AttachIcons
    }

}