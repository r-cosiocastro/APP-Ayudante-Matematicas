package com.rafaelcosio.mathhelper.models;

import org.parceler.Parcel;

@Parcel
public class Answer {
    private String desc;
    private int result;

    public Answer(String desc, int result) {
        this.desc = desc;
        this.result = result;
    }

    public Answer() {
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getDesc() {
        return desc;
    }

    public int getResult() {
        return result;
    }

}