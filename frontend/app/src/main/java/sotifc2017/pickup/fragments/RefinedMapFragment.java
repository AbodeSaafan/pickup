package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;

import com.google.android.gms.maps.MapFragment;

import sotifc2017.pickup.R;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;
import sotifc2017.pickup.fragment_managers.ConfigurableFragmentItemsManager;

/**
 * Created by chris on 2018-05-03.
 */

public class RefinedMapFragment extends MapFragment {

    int currentFragmentId = R.id.action_map;
    Activity activityForFragment;
    OnFragmentReplacement mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[RefinedMapFragment] must implement OnFragmentReplacement)");
        }
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId, false);

        activityForFragment = getActivity();
        setUpFragmentSpecificItems(activityForFragment, true);

        super.onResume();
    }

    @Override
    public void onStop() {
        setUpFragmentSpecificItems(activityForFragment, false);

        super.onStop();
    }

    private void setUpFragmentSpecificItems(Activity activityForFragment, boolean enable) {
        ImageView searchButton = activityForFragment.findViewById(R.id.toolbar_search_icon);
        ConfigurableFragmentItemsManager.enableVisibility(searchButton, enable);

        FloatingActionButton fabNewGame = activityForFragment.findViewById(R.id.fab_new_game);
        ConfigurableFragmentItemsManager.enableVisibility(fabNewGame, enable);
    }

}
