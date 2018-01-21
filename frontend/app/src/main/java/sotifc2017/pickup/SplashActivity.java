package sotifc2017.pickup;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import sotifc2017.pickup.api.Authentication;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            Authentication.getJwt(this);
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(this, ProfileSelfActivity.class);
            startActivity(mainIntent);
            finish();
        } catch (Exception e) {
            // JWT not present so user is not logged in
            Intent mainIntent = new Intent(this, SignInActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
