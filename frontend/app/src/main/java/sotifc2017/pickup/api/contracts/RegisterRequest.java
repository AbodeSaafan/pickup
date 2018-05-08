package sotifc2017.pickup.api.contracts;

import sotifc2017.pickup.api.enums.API_GENDER;

/**
 * Created by Abode on 12/3/2017.
 */

public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String fname;
    private String lname;
    private API_GENDER gender;
    private String dob;

    public RegisterRequest(String email, String password, String username, String fname,  String lname, API_GENDER gender, String dob){
        this.email = email;
        this.password = password;
        this.username = username;
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.dob = dob;
    }
}