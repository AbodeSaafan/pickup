package sotifc2017.pickup.api.contracts;

/**
 * Created by parezina on 5/11/2018.
 */

public class SimpleJWTRequest {
    private String jwt;

    public SimpleJWTRequest(String jwt)
    {
        this.jwt = jwt;
    }

    public static SimpleJWTRequest CreateSimpleJWTRequest(String token) {
        SimpleJWTRequest request = new SimpleJWTRequest(token);
        return request;
    }
}
