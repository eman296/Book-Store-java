package com.sortscript.Book_Store;

public class Insert_ModelClass {
    String id,uid,username,email,phone,password,image;

    public Insert_ModelClass() {
    }

     public Insert_ModelClass(String id,String uid,String username, String email, String phone, String password, String image) {
        this.id = id;
         this.uid = uid;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.image = image;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
