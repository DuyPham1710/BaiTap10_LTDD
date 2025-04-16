package vn.duy.baitap10.model;

import java.util.Map;

public class VideoModel {
    private String title;
    private String desc;
    private String url;
    private Map<String, Boolean> likes;   // Likes của video (map người dùng đã thích)
    private Map<String, Boolean> dislikes; // Dislikes của video (map người dùng đã không thích)
    private String email;
    private String avatarOwner;

    public VideoModel() {
    }

    public VideoModel(String title, String desc, String url, Map<String, Boolean> likes, Map<String, Boolean> dislikes, String email, String avatarOwner) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.likes = likes;
        this.dislikes = dislikes;
        this.email = email;
        this.avatarOwner = avatarOwner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Boolean> getDislikes() {
        return dislikes;
    }

    public void setDislikes(Map<String, Boolean> dislikes) {
        this.dislikes = dislikes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarOwner() {
        return avatarOwner;
    }

    public void setAvatarOwner(String avatarOwner) {
        this.avatarOwner = avatarOwner;
    }
}
