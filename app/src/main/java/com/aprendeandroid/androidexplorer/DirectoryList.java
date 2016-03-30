package com.aprendeandroid.androidexplorer;

import android.content.Context;

public class DirectoryList {

    String textdir;
    String namedir;
    String imgdir;

    public DirectoryList(String textdir,String namedir, String imgdir){
        this.textdir = textdir;
        this.namedir = namedir;
        this.imgdir = imgdir;
    }

    public String getTextDir() { return this.textdir; }

    public String getNameDir() {
        return this.namedir;
    }

    public String getImgDir() { return this.imgdir;}

}
