package sotifc2017.pickup.api.contracts;


/**
 * Created by radhika on 2018-02-16.
 */

public class ChangePasswordRequest {
    private String jwt;
    private String old_password;
    private String new_password;



    public ChangePasswordRequest(String jwt, String oldPassword, String newPassword){
        this.jwt = jwt;
        this.old_password = oldPassword;
        this.new_password = newPassword;
    }
}
