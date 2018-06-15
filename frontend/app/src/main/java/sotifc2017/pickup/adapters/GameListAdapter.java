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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sotifc2017.pickup.R;
import sotifc2017.pickup.activities.HostingActivity;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.models.GameModel;
import sotifc2017.pickup.fragments.GameViewFragment;
import sotifc2017.pickup.helpers.helperForGameListItem;


/**
 * Created by rkrishnan on 3/13/2018.
 */

public class GameListAdapter extends BaseAdapter {

    private GameModel[] gamesList;
    private Activity mContext;
    private Geocoder geocoder;
    private helperForGameListItem helper;

    public GameListAdapter(Activity context, GameModel[] gameArrayList) {
        this.mContext = context;
        this.gamesList = gameArrayList;
        geocoder = new Geocoder(context, Locale.getDefault());
        helper = new helperForGameListItem();

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
        gameName.setText(game.name);

        TextView location = itemView.findViewById(R.id.location);
        location.setText(helper.getLocation(geocoder, game.location.get("lat"), game.location.get("lng")));


        TextView dateTime = itemView.findViewById(R.id.dateTime);
        TextView finalTime = itemView.findViewById(R.id.time);
        HashMap<String, String> date_time = new HashMap<String, String>();
        date_time = helper.getDate(game.start_time, game.end_time);
        if (date_time.get("finalTime") == "") {
            dateTime.setText(date_time.get("dateTime"));
        } else {
            dateTime.setText(date_time.get("dateTime"));
            finalTime.setText(date_time.get("finalTime"));
        }

        TextView players = itemView.findViewById(R.id.players);
        players.setText(helper.getPlayerCount(game.total_players_added, game.total_players_required));


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
        helper.setPlayerIcon(mContext, player_icon, game.total_players_required, game.total_players_added);

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
