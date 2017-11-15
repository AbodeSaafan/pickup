package sotifc2017.pickup;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.HashMap;

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

        authenticateUser(email, pass);
    }

    private void signInSuccess(String message) {
        Toast.makeText(this, "Sign in successsful: " + message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        progressDialog.cancel();
    }

    private void signInFailure(String message) {
        Toast.makeText(this, "Sign in failed: " + message, Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
    }


    private void authenticateUser(String email, String password) {
        // TODO beautify this proof of concept design
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);


        JsonObjectRequest loginRequest = new JsonObjectRequest
                (Request.Method.POST, HttpUtils.LOGIN_ENDPOINT, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            signInSuccess("jwt: " + response.getString("token") + " for user " + response.getString("user_id"));
                        }
                        catch (Exception e){
                            signInFailure(e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            JSONObject errorJSON = new JSONObject(new String(error.networkResponse.data, "UTF-8"));
                            signInFailure(errorJSON.getString("error"));
                        }
                        catch (Exception e){

                        }
                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        HttpUtils.getInstance(this).addToRequestQueue(loginRequest);
    }
}
