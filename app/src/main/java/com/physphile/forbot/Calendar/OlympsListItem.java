package com.physphile.forbot.Calendar;

class OlympsListItem {
    public String name;
    public String level;

    OlympsListItem(){}

    OlympsListItem(String _name, String _level) {
        this.level = _level;
        this.name = _name;
    }

    String getName() {
        return name;
    }

    String getLevel() {
        return level;
    }

    void setName (String name) {
        this.name = name;
    }

    void setLevel(String level) {
        this.level = level;
    }

}