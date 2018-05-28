package sotifc2017.pickup.fragment_interfaces;

/**
 * Created by chris on 2018-05-03.
 */

public interface OnFragmentReplacement {

    void configureMenuItemSelection(int currentFragmentId, boolean addTop);

    void startPlacePickerActivity();

    void clearMenuItemSelection();
}
