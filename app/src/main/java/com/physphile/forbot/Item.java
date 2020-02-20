package com.physphile.forbot;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

//наш класс элементов, которые мы будем пушить в БД

@IgnoreExtraProperties
class Item implements Serializable{
    public String name;
    public String level;

    public Item(){

    }

    Item (String _name, String _level) {
        this.level = _level;
        this.name = _name;
    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

}