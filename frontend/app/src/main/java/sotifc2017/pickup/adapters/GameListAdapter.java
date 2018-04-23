package sotifc2017.pickup.Adapters;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
    Activity mContext;
    Geocoder geocoder;

    public GameListAdapter(Activity context, ArrayList<GameModel> gameArrayList) {
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
            itemView = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_game_list_item,null);
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
        String date = "";

        SimpleDateFormat sdf_date = new SimpleDateFormat("d");
        if(date.endsWith("1") && !date.endsWith("11"))
            sdf_date = new SimpleDateFormat("EE MMM d'st' yyyy");
        else if(date.endsWith("2") && !date.endsWith("12"))
            sdf_date = new SimpleDateFormat("EE MMM d'nd' yyyy");
        else if(date.endsWith("3") && !date.endsWith("13"))
            sdf_date = new SimpleDateFormat("EE MMM d'rd' yyyy");
        else
            sdf_date = new SimpleDateFormat("EE MMM d'th' yyyy");


        String start_date = sdf_date.format(new Date(game.start_time * 1000L));
        String end_date = sdf_date.format(new Date(game.end_time * 1000L));

        DateFormat time_format = new SimpleDateFormat("h:mm a");
        time_format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        String start_time = time_format.format(new Date(game.start_time*1000L));
        String end_time = time_format.format(new Date(game.end_time*1000L));

        if (start_date.equals(end_date)) {
            date = start_date + ", " + start_time + "-" + end_time;
        }

        gameName.setText(game.name);
        location.setText(newLocation);
        players.setText(playerCount);
        dateTime.setText(date);

        if (game.player_restricted) {
            ImageButton warning  = (ImageButton) itemView.findViewById(R.id.warning);
            warning.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //popup toast
                    Toast.makeText(v.getContext(), "Cannot join game", Toast.LENGTH_SHORT).show();
                }
            });


        }


        return itemView;
    }
}
