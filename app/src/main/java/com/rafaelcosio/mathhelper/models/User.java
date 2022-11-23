package com.rafaelcosio.mathhelper.models;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class User {
    private static final int LOCAL = 0;
    private static final int GOOGLE = 1;
    private static final int FACEBOOK = 2;
    private static final int EMAIL = 3;

    private Long _id;
    private String uid;
    private String name;
    private String email;
    private int level;
    private int points;
    private @Type
    int type;


    public User() {
    }

    public User(String uid, String name, String email, int level, int points, int type) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.level = level;
        this.points = points;
        this.type = type;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id=" + _id +
                ", _uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", points=" + points +
                ", type=" + type +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Type
    public int getType() {
        return type;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCAL, GOOGLE, FACEBOOK, EMAIL})
    public @interface Type {
    }
}
