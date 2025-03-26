package com.example.asm_adr;

public class ItemModel {
    private String title;  // Text for the item
    private int imageResource;  // Drawable resource ID for the image

    // Constructor
    public ItemModel(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    // Getter methods to access the data
    public String getTitle() {
        return title;
    }

    public int getImageResource() {
        return imageResource;
    }
}

