package com.gd.codefury;

public class Image {
    private String imageUrl;
    private String title;
    private String description;
    private String link;


    public Image(String imageUrl, String title, String description, String link) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.description = description;
        this.link = link;
    }

    public String getImageUrl() { return imageUrl; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLink() { return link; }
}
