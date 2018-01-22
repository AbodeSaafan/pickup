package sotifc2017.pickup.api.contracts;

/**
 * Created by Abode on 12/3/2017.
 */

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password){
        this.email = email;
        this.password = password;
    }
}
