package sotifc2017.pickup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    public void newAccount(View view) {
        Toast.makeText(this, "New Account...", Toast.LENGTH_SHORT).show();
    }

    public void signIn(View view) {
        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
    }
}
