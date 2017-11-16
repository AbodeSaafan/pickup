package sotifc2017.pickup;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import org.json.JSONObject;
import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Utils;

public class SignInActivity extends AppCompatActivity {

    private EditText emailText;
    private String email;
    private EditText passText;
    private String pass;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailText = (EditText) findViewById(R.id.emailEditText);
        passText = (EditText) findViewById(R.id.passEditText);

        progressDialog = new ProgressDialog(SignInActivity.this,
                R.style.AppTheme_Dark);
    }

    public void newAccount(View view) {
        Toast.makeText(this, "New Account...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {

        //Close keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        Window window = progressDialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        progressDialog.setCancelable(false);
        progressDialog.show();

        email = emailText.getText().toString();
        pass = passText.getText().toString();

        Utils.getInstance(this).addToRequestQueue(Authentication.login_request(email, pass, successful_signin, error_signin));
    }

    private Response.Listener<JSONObject> successful_signin = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                signInSuccess(response.getString("token"), response.getString("refresh"));
            }
            catch (Exception e){ signInFailure(e.getMessage()); }

        }
    };

    private Response.ErrorListener error_signin =  new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                signInFailure(errorJSON.getString("error"));
            }
            catch (Exception e){ signInFailure(e.getMessage()); }
        }
    };

    private void signInSuccess(String jwt, String refresh) {
        Toast.makeText(this, "Sign in successsful", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = this.getSharedPreferences(
                "sotifc2017.pickup", Context.MODE_PRIVATE);
        prefs.edit().putString("jwt", jwt);

        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        progressDialog.cancel();

    }

    private void signInFailure(String message) {
        Toast.makeText(this, "Sign in failed: " + message, Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
    }


}
