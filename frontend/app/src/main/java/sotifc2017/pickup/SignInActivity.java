package sotifc2017.pickup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    private EditText emailText;
    private String email;
    private EditText passText;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailText = (EditText) findViewById(R.id.emailEditText);
        passText = (EditText) findViewById(R.id.passEditText);
    }

    public void newAccount(View view) {
        Toast.makeText(this, "New Account...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {
        email = emailText.getText().toString();
        pass = passText.getText().toString();

        if (this.authenticateUser(email, pass)) {
            signInSuccess();
        }
    }

    private void signInSuccess() {
        Toast.makeText(this, "Sign in successsful " + email + " : " + pass, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    private boolean authenticateUser(String email, String password) {
        return true;
    }
}
