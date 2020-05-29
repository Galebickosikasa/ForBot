package com.physphile.forbot.news

class NewsFirebaseItem {
    var title: String? = null
    var uri: String? = null
    var text: String? = null
    var author: String? = null
    var date: String? = null
    var number: Int? = null
    var mask: Int? = null
    var coolDate: Long = 0

    constructor() {}

    private fun getLastNum(t: String?): Int {
        var s = t
        var ans = ""
        while (!s!!.isEmpty() && s[s.length - 1] != '.') {
            ans += s[s.length - 1]
            s = s.dropLast(1)
        }
        ans = ans.reversed()
        return ans.toInt()
    }

    private fun makeCoolDate() {
        var t = date
        val a = getLastNum(t)
        while (t!![t.length - 1] != '.') t = t.dropLast(1)
        t = t.dropLast(1)
        val b = getLastNum(t)
        while (t!![t.length - 1] != '.') t = t.dropLast(1)
        t = t.dropLast(1)
        val c = getLastNum(t)
        coolDate = -(c + b * 32 + a * 366).toLong()
    }

    constructor(title: String?, uri: String?, text: String?, author: String?, date: String?, number: Int?, mask: Int?) {
        this.title = title
        this.uri = uri
        this.text = text
        this.author = author
        this.date = date
        this.number = number
        this.mask = mask
        makeCoolDate()
    }

}