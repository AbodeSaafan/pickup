package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.contracts.GetSearchRequest;
import sotifc2017.pickup.fragment_interfaces.SearchFragment;

/**
 * Created by Abode on 3/3/2018.
 */

public class SearchUsersFragment extends Fragment implements SearchFragment {

    private EditText username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_users, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        username = view.findViewById(R.id.username_search_edittext);
    }

    @Override
    public GetSearchRequest constructSearchRequest(String jwt) {
        return GetSearchRequest.CreateUserRequest(jwt, username.getText().toString());
    }
}
