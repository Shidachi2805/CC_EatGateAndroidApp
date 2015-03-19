package de.eatgate.placessearch.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import de.eatgate.placessearch.R;

public class MakeReviewActivity extends ActionBarActivity {

    private Button btnSendRev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_review);
        btnSendRev = (Button)findViewById(R.id.btnSndRev);

       /* Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        Button btnSendRev = (Button)findViewById(R.id.btnSndRev);
        if(width>200) {
            btnSendRev.setWidth(200);
        }
        else {
            btnSendRev.setWidth(width);
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_back:
                Intent intent = new Intent(this, PlaceMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_close:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


     public void onClickSendRev(View button) {
         RatingBar ratingBarRev = (RatingBar)findViewById(R.id.ratingSingleReview);
         double valueRatingBar = ratingBarRev.getRating();
         Toast.makeText(getBaseContext(), "Your rating: " + valueRatingBar, Toast.LENGTH_LONG).show();
     }
}
