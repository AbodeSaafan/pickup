package sotifc2017.pickup.adapters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.models.PlayersModel;
import sotifc2017.pickup.api.models.UserModel;
import sotifc2017.pickup.fragments.ExtendedProfileFragment;

/**
 * Created by parezina on 5/7/2018.
 */

public class UserListAdapterT extends BaseAdapter {
    private UserModel[] gamesPlayersList;
    private Activity mContext;

    public UserListAdapterT(Activity context, UserModel[] userArrayList){
        this.gamesPlayersList = userArrayList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return gamesPlayersList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final UserModel user = gamesPlayersList[position];
        View itemView = convertView;


        if (itemView == null) {
            itemView = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_user_list_item, null);
        }

        TextView username = (TextView) itemView.findViewById(R.id.username);
        final String user_id = String.valueOf(user.user_id);

        username.setText(user.username);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup toast
                Bundle bundle = new Bundle();
                bundle.putString("userID", user_id);

                ExtendedProfileFragment extendedViewProfileFragment = new ExtendedProfileFragment();
                extendedViewProfileFragment.setArguments(bundle);

                replaceFragment(extendedViewProfileFragment, true, -1);

            }
        });

        return itemView;
    }

    private void replaceFragment(Fragment frag, boolean backStackAdd, int fragId){
        FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction.replace(R.id.fragment_container, frag);
        if(backStackAdd) transaction.addToBackStack(String.valueOf(fragId));

        // Commit the transaction
        transaction.commit();
    }


}
