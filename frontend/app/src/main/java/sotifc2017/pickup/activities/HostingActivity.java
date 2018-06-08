package sotifc2017.pickup.activities;

import android.app.Fragment;

/**
 * Created by Abode on 4/29/2018.
 */

public interface HostingActivity {

    void onDisplayGameSearchResults(String gameListJson);
    void onDisplayUserSearchResults(String userListJson);
    void replaceFragment(Fragment frag, boolean backStackAdd, int fragId);
}
