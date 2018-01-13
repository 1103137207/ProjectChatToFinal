package com.example.no24519.projectchat;

/**
 * Created by no24519 on 2017/12/6.
 */

public class Friends {

    private String date;
    private String thumb_image;

    public Friends(){

    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }


    public Friends(String date){this.date = date;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
