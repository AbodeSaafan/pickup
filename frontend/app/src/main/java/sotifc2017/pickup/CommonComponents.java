package sotifc2017.pickup;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;

/**
 * Created by Abode on 4/28/2018.
 */

public class CommonComponents {
    private CommonComponents(){}

    public static ProgressDialog getLoadingProgressDialog(Context ctx){
        ProgressDialog progressDialog = new ProgressDialog(ctx,
                R.style.AppTheme_Dark);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");

        Window window = progressDialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        progressDialog.setCancelable(false);

        return progressDialog;
    }
}
