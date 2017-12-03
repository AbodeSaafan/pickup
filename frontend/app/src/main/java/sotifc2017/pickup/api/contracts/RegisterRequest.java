package sotifc2017.pickup.api.contracts;

/**
 * Created by Abode on 12/3/2017.
 */

public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String fname;
    private String lname;
    private String gender;
    private String dob;

    public RegisterRequest(String email, String password, String username, String fname,  String lname, String gender, String dob){
        this.email = email;
        this.password = password;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.dob = dob;
    }
}