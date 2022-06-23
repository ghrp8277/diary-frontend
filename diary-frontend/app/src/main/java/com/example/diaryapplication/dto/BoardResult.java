package com.example.diaryapplication.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class BoardResult implements Serializable {
    // expose : object 중 해당 값이 null일 경우 json으로 만들 필드를 자동으로 생략해준다.
    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("content")
    private String content;

    @Expose
    @SerializedName("datetime")
    private Date datetime;

    @Expose
    @SerializedName("emotion_files")
    private ArrayList<EmotionFileResult> emotionFiles;

    public Integer getId () { return id; }

    public void setId (Integer id) { this.id = id; }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime (Date datetime) {
        this.datetime = datetime;
    }

    public ArrayList<EmotionFileResult> getEmotion_files () {
        return emotionFiles;
    }

    public void setEmotion_files (ArrayList<EmotionFileResult> emotionFiles) {
        this.emotionFiles = emotionFiles;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = "+id+", datetime = "+datetime+", emotion_files = "+emotionFiles+", title = "+title+", content = "+content+"]";
    }
}
