package com.example.notelearning;

public class Note {
    private String title;
    private String date;
    private boolean isBookmarked;

    public Note(String title, String date, boolean isBookmarked){
        this.title = title;
        this.date = date;
        this.isBookmarked = isBookmarked;
    }

    public String getTitle() {return title;}

    public String getDate() {return date;}

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }



}
