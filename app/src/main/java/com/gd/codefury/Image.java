package com.gd.codefury;

public class Image {
    private String image;
    private String title;
    private String description;
    private String link;
    private String user;

    // Required empty constructor for Firestore
    public Image() {}

    public Image(String image, String title, String description, String link, String user) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.link = link;
        this.user = user;
    }

    public String getImage() { return image; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLink() { return link; }
    public String getUser() { return user; }
}
