package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appyvet.materialrangebar.RangeBar;

import sotifc2017.pickup.R;

/**
 * Created by Abode on 3/3/2018.
 */

public class SearchGamesFragment extends Fragment {
    // Filters Section
    ImageButton filterToggleButton;
    RelativeLayout filterChildSection;

    CheckBox casualGameCheck;
    CheckBox seriousGameCheck;
    RangeBar minPlayerSeekBar;
    TextView minPlayerText;
    TextView skillRangeText;
    RangeBar skillRange;

    // Game details section
    ImageButton detailsToggleButton;
    RelativeLayout detailsChildSection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search_games, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //region Filters section
        casualGameCheck = view.findViewById(R.id.casualGameTypeCheckBox);
        seriousGameCheck = view.findViewById(R.id.seriousGameTypeCheckBox);

        minPlayerSeekBar = view.findViewById(R.id.min_players_seekbar);
        minPlayerText = view.findViewById(R.id.minimum_players_label);
        minPlayerText.setText(String.format(getString(R.string.game_search_minimum_player_message), 20));

        skillRange = view.findViewById(R.id.skill_seekBar);
        skillRangeText = view.findViewById(R.id.skill_level_label);
        skillRangeText.setText(String.format(getString(R.string.game_search_skill_range), 1, 10));


        filterToggleButton = view.findViewById(R.id.filters_header_toggle);
        filterChildSection = view.findViewById(R.id.filters_child_section);

        filterToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterChildSection.isShown()) {
                    filterToggleButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24dp));
                    filterChildSection.animate().translationY(-1 * filterChildSection.getHeight()).setDuration(300).alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            filterChildSection.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    filterToggleButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24dp));
                    filterChildSection.animate().translationY(0).setDuration(300).alpha(1.0f).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            filterChildSection.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

        seriousGameCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean seriousGameChecked) {
                skillRange.setEnabled(seriousGameChecked);
                /*if(!seriousGameChecked){ // Uncomment this section if we want to reset the skill range when serious game is disabled
                    skillRange.setRangePinsByIndices(0, skillRange.getTickCount() - 1);
                }*/
            }
        });

        minPlayerSeekBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                minPlayerText.setText(String.format(getString(R.string.game_search_minimum_player_message), Integer.parseInt(rightPinValue)));
            }
        });

        skillRange.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                skillRangeText.setText(String.format(getString(R.string.game_search_skill_range), Integer.parseInt(leftPinValue), Integer.parseInt(rightPinValue)));
            }

        });
        //endregion
        detailsChildSection = view.findViewById(R.id.details_child_section);
        detailsToggleButton = view.findViewById(R.id.details_header_toggle);

        //region game details section
        detailsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (detailsChildSection.isShown()) {
                    detailsToggleButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_more_black_24dp));
                    detailsChildSection.animate().translationY(-1 * detailsChildSection.getHeight()).setDuration(300).alpha(0.0f).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            detailsChildSection.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    detailsToggleButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_expand_less_black_24dp));
                    detailsChildSection.animate().translationY(0).setDuration(300).alpha(1.0f).withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            detailsChildSection.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        //endregion

        super.onViewCreated(view, savedInstanceState);
    }


}
