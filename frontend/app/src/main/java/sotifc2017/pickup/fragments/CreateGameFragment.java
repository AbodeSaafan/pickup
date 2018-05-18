package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;

public class CreateGameFragment extends Fragment implements GetJwt.Callback {

    int currentFragmentId = R.id.action_create_game;
    OnFragmentReplacement mCallback;

    GameModel gameModel = new GameModel();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentReplacement) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "[CreateGameFragment] must implement OnFragmentReplacement)");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetJwt(this).execute(getActivity());

        initGameModel(gameModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_game, container, false);
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId, true);

        super.onResume();
    }

    @Override
    public void jwtSuccess(String jwt) {
    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
    }

    private void initGameModel(GameModel gameModel) {
        // Init game details we know on creation of a new game
    }

    public static void onAgeRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch(view.getId()) {
                case R.id.radio_18_range:
                    break;
                case R.id.radio_25_range:
                    break;
                case R.id.radio_35_range:
                    break;
                case R.id.radio_45_range:
                    break;
            }
        }
    }

    public static void onGenderRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch(view.getId()) {
                case R.id.radio_male_gender:
                    break;
                case R.id.radio_female_gender:
                    break;
                case R.id.radio_other_gender:
                    break;
            }
        }
    }

}
