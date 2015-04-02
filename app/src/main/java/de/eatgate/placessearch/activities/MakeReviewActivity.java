package de.eatgate.placessearch.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.global.AppGob;

public class MakeReviewActivity extends Activity {

    private final String server = "http://192.168.70.22/EatGate/api/WWWBewertungPortal";
    private Button btnSendRev;
    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;
    private ProgressDialog statusProgress = null;
    private AppGob app;

    /**
     * da static ausserhalb der inneren Klasse
     *
     * @param url
     * @param revJSON
     * @return
     */
    private static int postReview(String url, JSONObject revJSON, Activity ac) {
        InputStream inputStream = null;
        String result = "";
        int responseCode = 0;
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";
            // 4. convert JSONObject to JSON to String
            json = revJSON.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            StatusLine statusLine = httpResponse.getStatusLine();
            responseCode = statusLine.getStatusCode();

            // 10. convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.e("JSON Result Review", e.getLocalizedMessage());
        }

        Log.i("JSON Result Review", result);


        // 11. return result Response vom Server
        return responseCode;
    }

    /**
     * da static ausserhalb der inneren Klasse
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private void buildInfoDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("About");
        builder.setMessage("EatGate Version 1.0 2015");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // nichts weiter tun; Dialog schließen
                dialog.dismiss();
            }
        });
        builder.setCancelable(false); // nicht schließen mit ZURÜCK-Button
        dialog = builder.create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_review);
        buildInfoDialog();
        app = (AppGob) getApplication();
        btnSendRev = (Button) findViewById(R.id.btnSndRev);

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
        getMenuInflater().inflate(R.menu.menu_main_ex, menu);
        ActionBar bar = getActionBar(); // or MainActivity.getInstance().getActionBar()
        // Alternative, falls XML-Styles fuer Actionbar nicht funktionieren via Code
        // Style der Actionbar veraendern
        // bar.setBackgroundDrawable(new ColorDrawable(0xff00DDED));
        TextView titleView = (TextView) findViewById(R.id.action_bar_title);
        if (titleView == null) {
            int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
            titleView = (TextView) findViewById(titleId);
        }
        titleView.setText("EatGate");
        titleView.setTextColor(Color.WHITE);
        // another solution which did work
        // Spannable text = new SpannableString(bar.getTitle());
        // text.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // bar.setTitle(text);
        // alternative Loesung, die funktioniert
        // bar.setTitle((Html.fromHtml("<font color=\"#FF4444\">" + "EatGate"+ "</font>")));
        bar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
        bar.setDisplayShowTitleEnabled(true);
        return true;
    }

    @Override
    /**
     * reagiert auf die Klicks in der Actionbar
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        View menuItemView = findViewById(R.id.action_menu);
        if (id == R.id.action_menu) {
            onClickMainMenu(menuItemView);
        } else if (id == R.id.action_back) {
            // Intent intent = new Intent(this, SinglePlacesActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Erzeugt das Popup Menu und registriert den ClickListener fuer das Submenu
     *
     * @param v
     */
    public void onClickMainMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main_popsub, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_info:
                        dialog.show();
                        return true;
                    case R.id.action_close:
                        // toDo SinglePlacesActivity beenden zusätzlich
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    public void onClickSendRev(View button) {
        statusProgress = ProgressDialog.show
                (this, "Bitte warten ...", "Review wird verarbeitet ...", true, false);
        new SendReview(this).execute(server);
        // Intent intent = new Intent(this, SinglePlacesActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // startActivity(intent);
        // finish();
    }

    /**
     * Helper class for Registration
     */
    private class SendReview extends AsyncTask<String, Void, Integer> {
        private Activity mActivity;

        public SendReview(Activity ac) {
            mActivity = ac;
        }

        /**
         * Erledige bevor Ausführen von Task
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... urls) {
            TextView headlineView = (TextView) mActivity.findViewById(R.id.editTexHeadline);
            TextView contentView = (TextView) mActivity.findViewById(R.id.editTextReview);
            RatingBar ratingBarRev = (RatingBar) mActivity.findViewById(R.id.ratingSingleReview);
            double valueRatingBar = ratingBarRev.getRating();
            // erzeuge JSON zum senden
            JSONObject reviewJSON = new JSONObject();
            try {
                reviewJSON.put("Service", "AddBewertung");
                reviewJSON.put("Ueberschrift", headlineView.getText());
                reviewJSON.put("Inhalt", contentView.getText());
                reviewJSON.put("Erstelltlungdatum", new Date().toString());
                reviewJSON.put("Voting", "" + valueRatingBar);
                reviewJSON.put("idBenutzer", "" + app.mUserId); // toDo dynamischer Benutzer
                reviewJSON.put("Place_id", app.g_placeDetails.getPlace_id());
                Log.e("reviewJSON check", reviewJSON.toString());
            } catch (Exception ex) {
                // toDo
                Log.e("JSON Exception", "doInBackground Fehler");
            }
            // Toast.makeText(getBaseContext(), "Txt", Toast.LENGTH_LONG).show();
            // ausführen des Requests an den Server
            int responseCd = postReview(urls[0], reviewJSON, mActivity);
            Log.i("JSON Log", "" + responseCd);
            // Toast.makeText(getBaseContext(), "" + responseCd, Toast.LENGTH_LONG).show();

            statusProgress.dismiss();
            return responseCd;
        }

        /**
         * Empty - Aufgabe wird von PostReview uebernommen
         */
        @Override
        protected void onPostExecute(Integer code) {
            Log.i("JSON Log", "onPostExecute " + code);
            if (code == 201) {
                Toast.makeText(MakeReviewActivity.this, "Succcess: " + code + "!", Toast.LENGTH_LONG).show();
                // Intent intent = new Intent(MakeReviewActivity.this, PlaceMapActivity.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // startActivity(intent);
                finish();
            } else {
                Toast.makeText(MakeReviewActivity.this, "Error! Try again!", Toast.LENGTH_LONG).show();
            }
        }
    }

}
