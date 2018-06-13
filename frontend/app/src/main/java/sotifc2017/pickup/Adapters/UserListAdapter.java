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
import sotifc2017.pickup.activities.HostingActivity;
import sotifc2017.pickup.api.models.UserModel;
import sotifc2017.pickup.fragments.ExtendedProfileFragment;

/**
 * Created by rkrishnan on 5/7/2018.
 */

public class UserListAdapter extends BaseAdapter {

    private UserModel[] userList;
    private Activity mContext;

    public UserListAdapter(Activity context, UserModel[] userArrayList) {
        this.mContext = context;
        this.userList = userArrayList;

    }

    @Override
    public int getCount() {
        return userList.length;
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

        final UserModel user = userList[position];
        View itemView = convertView;


        if (itemView == null) {
            itemView = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_user_list_item, null);
        }

        TextView firstLastName = itemView.findViewById(R.id.firstLastName);
        TextView username = itemView.findViewById(R.id.username);
        final String user_id = String.valueOf(user.user_id);

        firstLastName.setText(user.fname);
        username.setText(user.username);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup toast
                Bundle bundle = new Bundle();
                bundle.putString("userID", user_id);

                ExtendedProfileFragment extendedViewProfileFragment = new ExtendedProfileFragment();
                extendedViewProfileFragment.setArguments(bundle);

                ((HostingActivity) mContext).replaceFragment(extendedViewProfileFragment, true, -1);

            }
        });


        return itemView;
    }

}
