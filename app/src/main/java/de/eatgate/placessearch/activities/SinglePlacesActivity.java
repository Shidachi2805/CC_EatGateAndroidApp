package de.eatgate.placessearch.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import de.eatgate.placessearch.R;
import de.eatgate.placessearch.activities.PlaceMapActivity;
import de.eatgate.placessearch.entities.Review;
import de.eatgate.placessearch.global.AppGob;
import de.eatgate.placessearch.services.HttpFileUpload;

/**
 * Created by ProMarkt on 19.01.2015.
 */
public class SinglePlacesActivity extends Activity {

    private ListView listView_we_day;
    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_place);
        AppGob app = (AppGob) getApplication();
        if(app != null) {
            String str_name = app.g_placeDetails.getName();
            TextView tv_name = (TextView) findViewById(R.id.name);
            tv_name.setText(str_name);

            String str_adresse = app.g_placeDetails.getVicinity();
            TextView tv_adress = (TextView) findViewById(R.id.adresse);
            tv_adress.setText(str_adresse);

            String str_rating = "" + app.g_placeDetails.getRating();
            TextView tv_rating = (TextView) findViewById(R.id.rating);
            tv_rating.setText(str_rating);

            ArrayList<String> arrList =  app.g_placeDetails.getWeekdays();

            if(arrList != null)
            {
                Log.i("singlePlace", "arraylist: " + arrList.size());
                TextView tv_OpenHours = (TextView) findViewById(R.id.str_oppenHours);
                tv_OpenHours.setVisibility(View.VISIBLE);
                listView_we_day = (ListView)findViewById(R.id.weekday_list);
                listAdapter = new ArrayAdapter<String>(this,R.layout.simplerow,arrList);
                listView_we_day.setAdapter(listAdapter);
            }

            ArrayList<Review> reviews = app.g_placeDetails.getArrRev();

            if(reviews != null)
            {
                Log.i("reviews", "Reviews: " + reviews.get(0).getText());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionbar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void OnClickUploadFile(View view){
       try {
            // Set your file path here

            File path = Environment.getDataDirectory();
            String str = "DCIM/bildtest.jpg";
            Toast.makeText(getBaseContext(), "Your File Path:  " + str, Toast.LENGTH_LONG).show();
            FileInputStream fstrm = new FileInputStream(str);

            // Set your server page url (and the file title/description)
         //   HttpFileUpload hfu = new HttpFileUpload("http://www.myurl.com/fileup.aspx", "my file title","my file description");

          //  hfu.Send_Now(fstrm);

       } catch (FileNotFoundException e) {
            // Error: File not found
           Log.e("F***", "***");
       }
    }

    public void onClickWriteRev(View button) {
        Intent intent = new Intent(this, MakeReviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
//    private class ListViewAdapter_Weekday extends ArrayAdapter<String> {
//        private Context context;
//        private List<String> tag = new ArrayList<String>();
//        public ListViewAdapter_Weekday(Context context, List<String> tag) {
//            super(context, R.layout.single_place, tag);
//            this.context = context;
//            this.tag = tag;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            LayoutInflater inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View row = inflater.inflate(R.layout.single_place, null, false);
//
//            // ImageView icon = (ImageView) row.findViewById(R.id.iv_gender);
//            // LinearLayout linLayout = (LinearLayout) row.findViewById(R.id.listPlacesLayout);
//            TextView name = (TextView) row.findViewById(R.id.tag);
//            //TextView age = (TextView) row.findViewById(R.id.age_value);
//            // TextView separator = (TextView) row.findViewById(R.id.seperator);
//
//            name.setText(tag.indexOf(position));
//
//            return row;
//        }
//    }
}
