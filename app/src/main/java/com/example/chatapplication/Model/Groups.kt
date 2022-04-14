package com.example.chatapplication.Model

class Groups {
    private var Profile: String = ""
    private var Search: String = ""
    private var Groupname: String = ""
    private var GroupDescription: String = ""
    private var Admin: String = ""
    private var Members: ArrayList<String> = ArrayList()

    constructor()
    constructor(Groupname: String, Profile: String, Search: String,  Admin: String, GroupDescription: String, Member: ArrayList<String>) {
        this.Profile = Profile
        this.Search = Search
        this.Groupname = Groupname
        this.Admin = Admin
        this.GroupDescription = GroupDescription
        this.Members = Members
    }


    fun getProfile(): String? {
        return Profile
    }
    fun getSearch(): String? {
        return Search
    }
    fun getGroupname(): String? {
        return Groupname
    }
    fun getDescription(): String? {
        return GroupDescription
    }
    fun getAdmin(): String? {
        return Admin
    }
    fun getMembers(): ArrayList<String>{
        return Members
    }
}