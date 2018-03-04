package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sotifc2017.pickup.R;

/**
 * Created by Abode on 3/3/2018.
 */

public class SearchGamesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_games, container, false);
    }
}
