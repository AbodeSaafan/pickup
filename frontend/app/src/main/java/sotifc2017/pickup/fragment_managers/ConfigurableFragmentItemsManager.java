package sotifc2017.pickup.fragment_managers;

import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import static sotifc2017.pickup.Common.Defaults.FC_TAG;

/**
 * Created by chris on 2018-05-03.
 */

public class ConfigurableFragmentItemsManager {

    public static void enableVisibility(ImageView object, boolean enable) {
        if (object != null) {
            int visibility = enable ? View.VISIBLE : View.INVISIBLE;
            object.setVisibility(visibility);
        } else {
            Log.d(FC_TAG, "[FragmentItemsManager][enableVisibility] View object not found");
        }
    }

    public static void configureMenuItemSelection(NavigationView navigationView, int currentFragmentId) {
        enableFullMenu(navigationView);

        MenuItem navItemSelected = navigationView.getMenu().findItem(currentFragmentId);

        if (navItemSelected != null) { // Fragment for menu item
            navItemSelected.setChecked(true).setEnabled(false);
        } else {
            Log.d(FC_TAG, "[FragmentItemsManager][configureMenuItemSelection] Not in menu");
        }
    }

    public static void enableFullMenu(NavigationView navigationView){
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false).setEnabled(true);
        }
    }

}
