package sotifc2017.pickup.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import sotifc2017.pickup.R;
import sotifc2017.pickup.api.GetJwt;

public class SplashActivity extends AppCompatActivity implements GetJwt.Callback {
    private final int RETRY_ATTEMPTS = 4;
    private int tries_left;
    private TextView splashInfoLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        splashInfoLabel = findViewById(R.id.splashInfoLabel);

        tries_left = RETRY_ATTEMPTS;
        new GetJwt(this).execute(this);
    }

    @Override
    public void jwtSuccess(String jwt) {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void jwtFailure(GetJwt.JwtOutcome outcome) {
        switch (outcome) {
            case BadJwtRetrieval:
            case NoRefresh:
                Intent mainIntent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(mainIntent);
                finish();
                break;
            case ServerFault:
            default:
                retryJwt();
        }
    }

    private void retryJwt() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (--tries_left > 0) {

                    new CountDownTimer((RETRY_ATTEMPTS - tries_left) * 5 * 1000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            splashInfoLabel.setText(getString(R.string.splashScreenRetryCountdown, (int) millisUntilFinished / 1000));
                        }

                        public void onFinish() {
                            new GetJwt(SplashActivity.this).execute(SplashActivity.this);
                        }
                    }.start();

                } else {
                    AlertDialog.Builder exitDialog = new AlertDialog.Builder(SplashActivity.this);
                    exitDialog.setMessage(getString(R.string.splashScreenServerDown));
                    exitDialog.setCancelable(true);

                    exitDialog.setPositiveButton(
                            "Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    System.exit(1);
                                }
                            });

                    exitDialog.create().show();
                }
            }
        });
    }
}
