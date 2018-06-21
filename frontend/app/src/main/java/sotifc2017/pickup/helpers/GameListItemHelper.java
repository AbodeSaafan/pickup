package sotifc2017.pickup.helpers;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.enums.ENFORCED_PARAMS;

public class GameListItemHelper {


    public HashMap<String, String> getDate(long start_time, long end_time) {

        HashMap<String, String> dateTime = new HashMap<String, String>();
        String date = "";
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

        String start_date = sdf_date.format(new Date(start_time * 1000L));
        String end_date = sdf_date.format(new Date(end_time * 1000L));


        //Calculate Time
        SimpleDateFormat time_format = new SimpleDateFormat("h:mm a");

        String startTime = time_format.format(new Date(start_time * 1000L));
        String endTime = time_format.format(new Date(end_time * 1000L));


        if (start_date.equals(end_date)) {
            date = start_date + ", " + startTime + "-" + endTime;
            dateTime.put("dateTime", date);
            dateTime.put("finalTime", "");
        } else {
            date = start_date + " to " + end_date;
            finalTime = startTime + "-" + endTime;
            dateTime.put("dateTime", date);
            dateTime.put("finalTime", finalTime);

        }

        return dateTime;
    }


    public String getPlayerCount(int total_players_added, int total_players_required) {

        String playerCount = "";
        playerCount = String.valueOf(total_players_added) + "/" + String.valueOf(total_players_required);

        return playerCount;
    }


    public String getLocation(Geocoder geocoder, double latitude, double longitude) {

        String location = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                if (addresses.get(0).getLocality() != null) {
                    location = location + addresses.get(0).getLocality() + ", ";
                }
                if (addresses.get(0).getAdminArea() != null) {
                    location = location + addresses.get(0).getAdminArea() + ", ";
                }
                if (addresses.get(0).getCountryCode() != null) {
                    location = location + addresses.get(0).getCountryCode();
                }
                if (addresses.get(0).getCountryCode() == null) {
                    location = location.substring(0, location.length() - 1);
                }

            } else {
                location = "N/A";
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
        return location;
    }


    public void setPlayerIcon(Activity mContext, ImageView player_icon, int total_players_required, int total_players_added) {

        int difference = total_players_required - total_players_added;

        if (difference >= 0 && difference <= 3) {

            player_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.red), PorterDuff.Mode.SRC_IN);

        } else if (difference >= 4 && difference <= 7) {

            player_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.light_orange), PorterDuff.Mode.SRC_IN);

        } else if (difference > 7) {
            player_icon.setColorFilter(ContextCompat.getColor(mContext, R.color.green), PorterDuff.Mode.SRC_IN);
        }

    }

    public String getGender(String gender)
    {
        if(gender.contains("f")) {
            return("Female Only");
        }
        else if (gender.contains("m")){
            return("Male Only");
        }
        return null;
    }



}
