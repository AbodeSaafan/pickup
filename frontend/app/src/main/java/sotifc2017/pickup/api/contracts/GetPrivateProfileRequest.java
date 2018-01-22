package sotifc2017.pickup.api.contracts;

/**
 * Created by Abode on 1/12/2018.
 */

public class GetPrivateProfileRequest {
    private String jwt;

    public GetPrivateProfileRequest(String jwt){
        this.jwt = jwt;
    }
}
