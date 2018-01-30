package sotifc2017.pickup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import sotifc2017.pickup.api.Authentication;

public class ProfileSelfActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_self);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.profile_self_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.self_profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_self_setting:
                viewSettings();
                break;
            case R.id.profile_self_sign_out:
                AlertDialog diaBox = AskOption();
                diaBox.show();
                break;
            case R.id.extended_profile:
                viewExtendedProfile();
                break;
        }
        return true;
    }

    private void redirectSignIn() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    private void viewExtendedProfile() {
        Intent intent = new Intent(this, ExtendedProfileFragment.class);
        intent.putExtra("userID", String.valueOf(28));
        startActivity(intent);
    }

    private void viewSettings() {
        Intent intent = new Intent(this, SettingsFragment.class);
        startActivity(intent);
    }


    private AlertDialog AskOption() {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle(getString(R.string.sign_out_title))
                .setMessage(getString(R.string.sign_out_message))

                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //sign out call
                        Authentication.logout(ProfileSelfActivity.this);
                        redirectSignIn();
                        dialog.dismiss();
                    }

                })


                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}
