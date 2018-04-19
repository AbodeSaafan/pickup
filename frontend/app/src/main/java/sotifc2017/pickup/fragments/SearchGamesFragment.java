package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import sotifc2017.pickup.R;

/**
 * Created by Abode on 3/3/2018.
 */

public class SearchGamesFragment extends Fragment {
    ImageButton toggleButton;
    RelativeLayout childSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search_games, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toggleButton = view.findViewById(R.id.filters_header_toggle);
        childSection = view.findViewById(R.id.filters_child_section);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (childSection.isShown()) {
                    childSection.animate().translationY(-1 * childSection.getHeight()).setDuration(300).alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            childSection.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    childSection.animate().translationY(0).setDuration(300).alpha(1.0f).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            childSection.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


}
