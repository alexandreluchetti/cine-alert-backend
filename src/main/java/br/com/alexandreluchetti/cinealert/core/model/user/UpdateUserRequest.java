package br.com.alexandreluchetti.cinealert.core.model.user;

public class UpdateUserRequest {

    private String name;
    private String password;

    public UpdateUserRequest(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
