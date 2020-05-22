package com.physphile.forbot.olympiads;

class OlympsListItem {
    public String name;
    public String level;
    public String uri;
    public String text;
    public Integer num;
    public Integer year;
    public Integer month;
    public Integer dayOfMonth;

    public OlympsListItem() {
    }

    public OlympsListItem(String name, String level, String uri, String text, Integer num, Integer year, Integer month, Integer dayOfMonth) {
        this.name = name;
        this.level = level;
        this.uri = uri;
        this.text = text;
        this.num = num;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDate() {
        return year + "." + (month + 1) + "." + dayOfMonth;
    }

    public String getPath () {
        return year + "/" + month + "/" + dayOfMonth + "/" + num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}