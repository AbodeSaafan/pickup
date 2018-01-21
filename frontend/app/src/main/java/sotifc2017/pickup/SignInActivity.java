package sotifc2017.pickup;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

        emailText = (EditText) findViewById(R.id.emailEditText);
        passText = (EditText) findViewById(R.id.passEditText);

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


        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        Window window = progressDialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        progressDialog.setCancelable(false);
        progressDialog.show();

        email = emailText.getText().toString();
        pass = passText.getText().toString();

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

        Intent intent = new Intent(this, ProfileSelfActivity.class);
        startActivity(intent);
        progressDialog.cancel();
        finish();

    }

    private void signInFailure(String message) {
        Toast.makeText(this, "Sign in failed: " + message, Toast.LENGTH_SHORT).show();
        progressDialog.cancel();
    }


}
