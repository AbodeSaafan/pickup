package sotifc2017.pickup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.GetJwt;
import sotifc2017.pickup.fragment_interfaces.OnFragmentReplacement;

public class CreateGameFragment extends Fragment implements GetJwt.Callback {

    int currentFragmentId = R.id.action_create_game;
    OnFragmentReplacement mCallback;

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
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_game, container, false);
    }

    @Override
    public void onResume() {
        mCallback.configureMenuItemSelection(currentFragmentId);

        super.onResume();
    }

    @Override
    public void jwtSuccess(String jwt) {
    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
    }

}
