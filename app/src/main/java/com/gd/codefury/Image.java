package com.gd.codefury;

public class Image {
    private String imageUrl;
    private String title;
    private String description;
    private String link;
    private String user;


    public Image(String imageUrl, String title, String description, String link,String user) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.link = link;
        this.user=user;
    }

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLink() { return link; }

    public String getUser() { return user;}
}
