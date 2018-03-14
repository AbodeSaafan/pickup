package sotifc2017.pickup.helper_classes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.models.GameModel;

/**
 * Created by rkrishnan on 3/13/2018.
 */

public class GameListAdapter extends BaseAdapter {

    ArrayList<GameModel> gamesList;
    Context mContext;

    //Geocoder geocoder;

    public GameListAdapter(Context context, ArrayList<GameModel> gameArrayList) {
        this.mContext = context;
        this.gamesList = gameArrayList;

    }

    @Override
    public int getCount() {
        return gamesList.size();
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

        GameModel game = gamesList.get(position);
        View itemView = convertView;
        String newLocation;
        List<Address> addresses;

        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.fragment_game_list_item,null);
        }


        TextView gameName = (TextView) itemView.findViewById(R.id.gameName);
        TextView location = (TextView) itemView.findViewById(R.id.location);
        TextView dateTime = (TextView) itemView.findViewById(R.id.dateTime);
        TextView players = (TextView) itemView.findViewById(R.id.players);
        /*
        if (! game.location.isEmpty()) {

            try {
                Double latitude = game.location.get("lat");
                Double longitude = game.location.get("lng");
                addresses  = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    newLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();
                } else {
                    newLocation = "N/A";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        */

        //int startTime = game.start_time;
        //int endTime = game.end_time;



        gameName.setText(game.name);
        //location.setText(game.newLocation);
        //tvStudAddress.setText(student.getAddress());


        return itemView;
    }
}
