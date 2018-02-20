package sotifc2017.pickup;

import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import sotifc2017.pickup.api.Authentication;
import sotifc2017.pickup.api.Utils;
import sotifc2017.pickup.api.contracts.LoginRequest;
import sotifc2017.pickup.api.contracts.LoginResponse;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        emailText = findViewById(R.id.emailEditText);
        passText = findViewById(R.id.passEditText);

        passText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
             @Override
             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_DONE) {
                                        signIn(v);
                                    }
                                return false;
                            }
         });
        progressDialog = new ProgressDialog(SignInActivity.this,
                R.style.AppTheme_Dark);

    }

    public void newAccount(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void signIn(View view) {

        //Close keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        View focusView = null;
        email = emailText.getText().toString();
        pass = passText.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            emailText.setError(null);
            emailText.setError(getString(R.string.error_field_required));
            focusView = emailText;
            focusView.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pass))
        {
            passText.setError(null);
            passText.setError(getString(R.string.error_field_required));
            focusView = passText;
            focusView.requestFocus();
            return;
        }

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        Window window = progressDialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        progressDialog.setCancelable(false);
        progressDialog.show();

        email = emailText.getText().toString().trim();
        pass = passText.getText().toString().trim();

        Utils.getInstance(this).getRequestQueue(this).add(Authentication.login_request(new LoginRequest(email, pass), successful_signin, error_signin));
    }

    private Response.Listener<JSONObject> successful_signin = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                signInSuccess(Utils.gson.fromJson(response.toString(), LoginResponse.class));
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

    private void signInSuccess(LoginResponse response) {
        Toast.makeText(this, "Sign in successsful", Toast.LENGTH_SHORT).show();

        Authentication.saveJwt(this, response.token);
        Authentication.saveRefresh(this, response.refresh);
        Authentication.saveUserId(this, response.user_id);

        if (checkPermissions()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            progressDialog.cancel();
            finish();
        }
    }

    private void signInFailure(String message) {
        Toast.makeText(this, "Sign in failed: " + message, Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
    }

    public boolean checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        progressDialog.cancel();
        finish();
    }
}
