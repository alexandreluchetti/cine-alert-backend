package br.com.alexandreluchetti.cinealert.core.model.user;

public class UserResponse {

    private String id;
    private String name;
    private String email;
    private String avatarUrl;
    private long totalReminders;
    private long sentReminders;

    public UserResponse(String id, String name, String email, String avatarUrl, long totalReminders, long sentReminders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.totalReminders = totalReminders;
        this.sentReminders = sentReminders;
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

    public long getTotalReminders() {
        return totalReminders;
    }

    public long getSentReminders() {
        return sentReminders;
    }
}
