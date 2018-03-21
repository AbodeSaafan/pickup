package sotifc2017.pickup.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.models.GameModel;

/**
 * Created by rkrishnan on 3/13/2018.
 */

public class GameListAdapter extends BaseAdapter {

    ArrayList<GameModel> gamesList;
    Context mContext;
    Geocoder geocoder;

    public GameListAdapter(Context context, ArrayList<GameModel> gameArrayList) {
        this.mContext = context;
        this.gamesList = gameArrayList;
        geocoder = new Geocoder(context, Locale.getDefault());

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


        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.fragment_game_list_item,null);
        }


        TextView gameName = (TextView) itemView.findViewById(R.id.gameName);
        TextView location = (TextView) itemView.findViewById(R.id.location);
        TextView dateTime = (TextView) itemView.findViewById(R.id.dateTime);
        TextView players = (TextView) itemView.findViewById(R.id.players);

        double latitude = game.location.get("lat");
        double longitude = game.location.get("lng");



        String newLocation = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                newLocation = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryCode();
            } else {
                newLocation = "N/A";
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

        String playerCount = String.valueOf(game.total_players_added) + "/" + String.valueOf(game.total_players_required);
        String dateAndTime = "";
        DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        Date startDate = new Date(game.start_time * 1000L);
        Date endDate = new Date(game.start_time * 1000L);
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String startTime = format.format(startDate);
        String endTime = format.format(endDate);

        dateAndTime = startTime + "-" + endTime;


        gameName.setText(game.name);
        location.setText(newLocation);
        players.setText(playerCount);
        dateTime.setText(dateAndTime);


        return itemView;
    }
}
