package com.example.notelearning;

public class Memos {
    private String date;
    private String title;
    private String content;
    public boolean isBookmarked;


    public Memos() {
    }

    public Memos(String date, String title, String content, boolean isBookmarked) {
        this.date = date;
        this.title = title;
        this.content = content;
        this.isBookmarked = isBookmarked;
    }

    public  String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public  String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public  boolean getIsBookmarked() {
        return this.isBookmarked;
    }

    public  void setIsBookmarked(boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }



}

