package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Locale;

import sotifc2017.pickup.R;

/**
 * Created by Abode on 3/3/2018.
 */

public class SearchGamesFragment extends Fragment {
    ImageButton toggleButton;
    RelativeLayout childSection;
    DiscreteSeekBar minPlayerSeekBar;
    TextView minPlayerText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search_games, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        minPlayerSeekBar = view.findViewById(R.id.min_players_seekbar);
        minPlayerText = view.findViewById(R.id.minimum_players_label);

        // Will generalize later
        toggleButton = view.findViewById(R.id.filters_header_toggle);
        childSection = view.findViewById(R.id.filters_child_section);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (childSection.isShown()) {
                    toggleButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24dp));
                    childSection.animate().translationY(-1 * childSection.getHeight()).setDuration(300).alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            childSection.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    toggleButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24dp));
                    childSection.animate().translationY(0).setDuration(300).alpha(1.0f).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            childSection.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        // Will generalize later

        minPlayerSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                minPlayerText.setText(String.format(Locale.CANADA, getString(R.string.game_search_minimum_player_message), value));
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {}
        });

        super.onViewCreated(view, savedInstanceState);
    }


}
