package sotifc2017.pickup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

/**
 * Created by radhika on 2018-01-14.
 */

public class ExtendedProfileActivity extends AppCompatActivity {

    TextView age;
    TextView gender;
    TextView skillevel;
    TextView location;
    TextView averageReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extended_profile);
        age = (TextView)findViewById(R.id.ageValue);
        age.setText("21");
        gender = (TextView)findViewById(R.id.genderValue);
        gender.setText("Female");
        skillevel = (TextView)findViewById(R.id.skillLevelValue);
        skillevel.setText("Rookie");
        location = (TextView)findViewById(R.id.locationValue);
        location.setText("Mississauga");
        averageReview = (TextView)findViewById(R.id.averageReviewValue);
        averageReview.setText("2.5");
    }


}
