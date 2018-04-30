package sotifc2017.pickup.activities;

import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.api.models.UserModel;

/**
 * Created by Abode on 4/29/2018.
 */

public interface HostingActivity {

    public void onDisplayGameSearchResults(String gameListJson);
    public void onDisplayUserSearchResults(String userListJson);
}
