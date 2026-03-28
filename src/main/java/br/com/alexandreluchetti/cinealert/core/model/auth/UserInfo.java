package br.com.alexandreluchetti.cinealert.core.model.auth;

public class UserInfo {

    private String id;
    private String name;
    private String email;
    private String avatarUrl;

    public UserInfo(String id, String name, String email, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
