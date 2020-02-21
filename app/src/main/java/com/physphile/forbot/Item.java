package com.physphile.forbot;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.annotations.NotNull;

import java.io.Serializable;

//наш класс элементов, которые мы будем пушить в БД

@IgnoreExtraProperties
public class Item implements Serializable{
    private String name;
    private String level;

    public Item(){

    }

    public Item (String _name, String _level) {
        this.level = _level;
        this.name = _name;
    }

    public String getName() {
        return name;
    }

    public String getLevel() {
        return level;
    }

    public void setName (String name) {
        this.name = name;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}