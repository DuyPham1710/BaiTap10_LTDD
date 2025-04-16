package vn.duy.baitap10.model;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String fullName;
    private String email;
    private String password;
    private String avatarUrl;

    public User() {
    }

    public User(String fullName, String email, String avatarUrl) {
        this.fullName = fullName;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public User(String id, String fullName, String email, String password, String avatarUrl) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
