package com.appunite.example.debugutilsexample.model;

public class Owner {

    private String login;
    private Integer id;
    private String avatarUrl;
    private String htmlUrl;
    private String type;

    public Owner(String login, Integer id, String avatarUrl, String htmlUrl, String type) {
        this.login = login;
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.htmlUrl = htmlUrl;
        this.type = type;
    }

    public String getLogin() {
        return login;
    }

    public Integer getId() {
        return id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getType() {
        return type;
    }

}
