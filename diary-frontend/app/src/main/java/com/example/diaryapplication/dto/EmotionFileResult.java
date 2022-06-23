package com.example.diaryapplication.dto;

// http://pojo.sodhanalibrary.com

import java.io.Serializable;

public class EmotionFileResult implements Serializable {
    private String image_file_path;

    public String getImage_file_path() {
        return image_file_path;
    }

    public void setImage_file_path(String image_file_path) {
        this.image_file_path = image_file_path;
    }

    @Override
    public String toString() {
        return "ClassPojo [image_file_path = "+image_file_path+"]";
    }
}
