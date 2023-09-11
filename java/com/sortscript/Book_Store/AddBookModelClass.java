package com.sortscript.Book_Store;

public class AddBookModelClass {

    public AddBookModelClass() {}


    String uid;
    String id;
    String image;
    String title;
    String author;
    String category;
    String description;
    String sellprice;
    String condition;

    public AddBookModelClass(String uid, String id, String image, String title, String author, String category, String description, String sellprice, String condition) {
        this.uid = uid;
        this.id = id;
        this.image = image;
        this.title = title;
        this.author = author;
        this.category = category;
        this.description = description;
        this.sellprice = sellprice;
        this.condition = condition;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSellprice(String sellprice) {
        this.sellprice = sellprice;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getUid() {
        return uid;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getSellprice() {
        return sellprice;
    }

    public String getCondition() {
        return condition;
    }
}
