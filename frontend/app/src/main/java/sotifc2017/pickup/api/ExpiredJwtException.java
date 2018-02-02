package sotifc2017.pickup.api;

/**
 * Created by Abode on 1/31/2018.
 */

public class ExpiredJwtException extends Exception {
    public ExpiredJwtException(String message){
        super(message);
    }
}
