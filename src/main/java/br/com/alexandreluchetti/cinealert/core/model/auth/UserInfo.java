package br.com.alexandreluchetti.cinealert.core.model.auth;

public class UserInfo {

    private Long id;
    private String name;
    private String email;
    private String avatarUrl;

    public UserInfo(Long id, String name, String email, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
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
