package sotifc2017.pickup.api.contracts;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rkrishnan on 1/16/2018.
 */

public class GetExtendedProfileRequest {
    private String jwt;
    private Map<String, Double> location = new HashMap<String, Double>();
    private int skill_level;

    public GetExtendedProfileRequest(String jwt, double lat, double lng, int skillevel){
        this.jwt = jwt;
        this.location.put("lat", lat);
        this.location.put("lng", lng);
        this.skill_level = skillevel;
    }
}
