package com.physphile.forbot.news

class NewsFirebaseItem {
    var title: String? = null
    var uri: String? = null
    var text: String? = null
    var author: String? = null
    var date: String? = null
    var number: Int? = null
        private set
    var mask: Int? = null

    constructor() {}
    constructor(title: String?, uri: String?, text: String?, author: String?, date: String?, number: Int?, mask: Int?) {
        this.title = title
        this.uri = uri
        this.text = text
        this.author = author
        this.date = date
        this.number = number
        this.mask = mask
    }



}