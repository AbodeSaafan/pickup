package sotifc2017.pickup;

import org.junit.Test;

import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.LoginRequest;

import static org.junit.Assert.*;

/**
 * Created by Abode on 12/6/2017.
 */

public class UtilsTest {

    @Test
    public void loginRequestToQueryParamsTest() throws Exception {
        LoginRequest req = new LoginRequest("abode@mail.com", "123456");
        assertEquals("?password=123456&email=abode%40mail.com", Utils.jsonToUrlParam(req));
    }
}
