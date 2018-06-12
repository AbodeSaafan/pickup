package sotifc2017.pickup.adapters;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.HostingActivity;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.fragments.GameViewFragment;

/**
 * Created by rkrishnan on 3/13/2018.
 */

public class GameListAdapter extends BaseAdapter {

    private GameModel[] gamesList;
    private Activity mContext;
    private Geocoder geocoder;

    public GameListAdapter(Activity context, GameModel[] gameArrayList) {
        this.mContext = context;
        this.gamesList = gameArrayList;
        geocoder = new Geocoder(context, Locale.getDefault());

    }

    @Override
    public int getCount() {
        return gamesList.length;
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        final GameModel game = gamesList[position];
        View itemView = convertView;


        if (itemView == null) {
            itemView = LayoutInflater.from(this.mContext).inflate(R.layout.fragment_game_list_item, null);
        }


        TextView gameName = itemView.findViewById(R.id.gameName);
        TextView location = itemView.findViewById(R.id.location);
        TextView dateTime = itemView.findViewById(R.id.dateTime);
        TextView players = itemView.findViewById(R.id.players);
        TextView time = itemView.findViewById(R.id.time);
        RelativeLayout player_info = itemView.findViewById(R.id.player_info);

        double latitude = game.location.get("lat");
        double longitude = game.location.get("lng");


        String newLocation = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    newLocation = newLocation + addresses.get(0).getLocality() + ", ";
                }
                if (addresses.get(0).getAdminArea() != null) {
                    newLocation = newLocation + addresses.get(0).getAdminArea() + ", ";
                }
                if (addresses.get(0).getCountryCode() != null) {
                    newLocation = newLocation + addresses.get(0).getCountryCode();
                }
                if (addresses.get(0).getCountryCode() == null) {
                    newLocation = newLocation.substring(0, newLocation.length()-1);
                }

            } else {
                newLocation = "N/A";
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

        String playerCount = String.valueOf(game.total_players_added) + "/" + String.valueOf(game.total_players_required);
        String date = "";
        int start_time  = Integer.parseInt(game.start_time);
        int end_time = Integer.parseInt(game.end_time);
        String finalTime = "";


        //Calculate Date
        SimpleDateFormat sdf_date = new SimpleDateFormat("d");
        if (date.endsWith("1") && !date.endsWith("11"))
            sdf_date = new SimpleDateFormat("EE MMM d'st' yyyy");
        else if (date.endsWith("2") && !date.endsWith("12"))
            sdf_date = new SimpleDateFormat("EE MMM d'nd' yyyy");
        else if (date.endsWith("3") && !date.endsWith("13"))
            sdf_date = new SimpleDateFormat("EE MMM d'rd' yyyy");
        else
            sdf_date = new SimpleDateFormat("EE MMM d'th' yyyy");

        sdf_date.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        String start_date = sdf_date.format( new Date(start_time*1000L));
        String end_date = sdf_date.format( new Date(end_time*1000L));

        System.out.println(start_date);
        System.out.println(end_date);

        //Calculate Time
        SimpleDateFormat  time_format = new SimpleDateFormat("h:mm a");
        time_format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        String startTime = time_format.format(new Date(start_time*1000L));
        String endTime = time_format.format(new Date(end_time*1000L));


        if (start_date.equals(end_date)) {
            date = start_date + ", " + startTime + "-" + endTime;
        }
        else {
            date = start_date + " to " + end_date;
            finalTime = startTime + "-" + endTime;

        }


        gameName.setText(game.name);
        location.setText(newLocation);
        players.setText(playerCount);
        dateTime.setText(date);

        if (finalTime != "") {
            time.setText(finalTime);
        }

        if (game.player_restricted) {
            ImageButton warning = itemView.findViewById(R.id.warning);
            warning.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //popup toast
                    Toast.makeText(v.getContext(), "Cannot join game", Toast.LENGTH_SHORT).show();
                }
            });


        }

        //changing colour of player icon (based on player stats)

        ImageView player_icon = itemView.findViewById(R.id.player_icon);

        int difference = game.total_players_required - game.total_players_added;

        if (difference >= 0 && difference <= 3){

            player_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.red), PorterDuff.Mode.SRC_IN);

        } else if (difference >= 4 && difference <= 7) {

            player_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.light_orange), PorterDuff.Mode.SRC_IN);

        } else if (difference > 7) {
            player_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.green), PorterDuff.Mode.SRC_IN);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //popup toast
                Bundle bundle = new Bundle();
                String gameJson = Utils.gson.toJson(gamesList[position]);
                bundle.putString("gameJson", gameJson);

                GameViewFragment gameViewFragment = new GameViewFragment();
                gameViewFragment.setArguments(bundle);

                ((HostingActivity) mContext).replaceFragment(gameViewFragment, true, -1);
            }
        });


        return itemView;
    }
}
