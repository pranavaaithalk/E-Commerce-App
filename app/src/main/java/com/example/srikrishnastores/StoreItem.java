package com.example.srikrishnastores;

public class StoreItem {
    private String name,id;
    private int price;
    private String category;
    private String imageUrl;

    // Empty constructor for Firebase
    public StoreItem() {
    }

    public StoreItem(String id, String name, int price, String category, String imageUrl) {
        this.id=id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }
    public void setId(String id){
        this.id=id;
    }

    // Getters
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }

    public String getId() {
        return id;
    }
}

