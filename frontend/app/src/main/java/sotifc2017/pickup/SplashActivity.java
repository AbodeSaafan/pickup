package sotifc2017.pickup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import sotifc2017.pickup.api.GetJwt;

public class SplashActivity extends AppCompatActivity implements GetJwt.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        new GetJwt(this).execute(this);
    }

    @Override
    public void jwtSuccess(String jwt) {
        Intent mainIntent = new Intent(this, MapActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void jwtFailure(Exception e) {
        Intent mainIntent = new Intent(this, SignInActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
