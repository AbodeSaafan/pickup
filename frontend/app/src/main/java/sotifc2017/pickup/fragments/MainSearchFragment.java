package sotifc2017.pickup.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import sotifc2017.pickup.R;
import sotifc2017.pickup.android_modified_source.FragmentPagerAdapter;

/**
 * Created by Abode on 3/3/2018.
 */

public class MainSearchFragment extends Fragment {
    private final static int SEARCH_GAMES_TAB_NUMBER = 0;
    private final static int SEARCH_USERS_TAB_NUMBER = 1;
    Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ViewPager vp = getView().findViewById(R.id.main_search_pager);
        vp.setAdapter(new SectionsPagerAdapter(getFragmentManager()));
        TabLayout tabLayout = getView().findViewById(R.id.search_tabLayout);
        tabLayout.setupWithViewPager(vp);

        searchButton = view.findViewById(R.id.main_search_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeSearch();
            }
        });
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case SEARCH_GAMES_TAB_NUMBER:
                    return new SearchGamesFragment();
                case SEARCH_USERS_TAB_NUMBER:
                    return new SearchUsersFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case SEARCH_GAMES_TAB_NUMBER:
                    return getString(R.string.main_search_games_tab_title);
                case SEARCH_USERS_TAB_NUMBER:
                    return getString(R.string.main_search_users_tab_title);
            }
            return null;
        }
    }

    private void executeSearch(){

    }

}