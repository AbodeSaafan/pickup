package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.GetJwt;

public class CreateGameFragment extends Fragment implements GetJwt.Callback {

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_game, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Search results here
        new GetJwt(this).execute(getActivity());
    }

    @Override
    public void jwtSuccess(String jwt) {
    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
    }

}
