package sotifc2017.pickup.api.contracts;

/**
 * Created by radhika on 2018-02-16.
 */

public class UpdatePrivateProfileRequest {

    private String jwt;
    private String email;
    private String username;
    private String fname;
    private String lname;
    private String gender;
    private String dob;
    private String password;

    public UpdatePrivateProfileRequest(String jwt, String email, String username, String fname, String lname, String gender, String dob, String password){
        this.jwt = jwt;
        this.email = email;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.dob = dob;
        this.password = password;
    }
}
