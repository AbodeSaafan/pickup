package sotifc2017.pickup.fragment_interfaces;

import sotifc2017.pickup.api.contracts.GetSearchRequest;

/**
 * Created by Abode on 4/28/2018.
 */

public interface SearchFragment {
    GetSearchRequest constructSearchRequest(String jwt);
}
