package de.eatgate.placessearch.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.global.AppGob;
import de.eatgate.placessearch.helpers.MultipartEntity;

public class UploadPhotoActivity extends Activity implements OnClickListener {
    private static final String TAG = "upload";
    private Button mTakePhoto;
    private String mCurrentPhotoPath;
    private ProgressDialog statusProgress = null;
    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;
    private String server = "http://192.168.70.22/EatGate/home/photoupload";
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
        setContentView(R.layout.activity_upload_photo);
        buildInfoDialog();
        uploadPhoto();
        mTakePhoto = (Button) findViewById(R.id.uploadButtonLc);
        mTakePhoto.setOnClickListener(this);
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
            Intent intent = new Intent(this, SinglePlacesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.uploadButtonLc:
                uploadPhoto();
                break;
        }
    }

    private void uploadPhoto() {
        statusProgress = ProgressDialog.show(this, "Bitte warten ...", "Foto wird auf dem Server gespeichert", true, false);
        new UploadTask().execute();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume: " + this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
    }

    private class UploadTask extends AsyncTask<Integer, Void, Integer> {

        protected Integer doInBackground(Integer... arg0) {
            int serverResponseCode = 0;
            AppGob app = (AppGob) getApplication();
            if (app == null) return null;
            mCurrentPhotoPath = app.mCurrentPhotoPath;
            String key = "myFile";
            if (mCurrentPhotoPath == null || mCurrentPhotoPath.isEmpty()) return null;
            try {
                FileInputStream fileInputStream = new FileInputStream(mCurrentPhotoPath);
                setProgress(0);

                DefaultHttpClient httpclient = new DefaultHttpClient();
                try {
                    HttpPost httppost = new HttpPost(server); // server path
                    MultipartEntity reqEntity = new MultipartEntity();
                    reqEntity.addPart(key, app.g_placeDetails.getPlace_id() + "_EatGate_" +
                            System.currentTimeMillis() + ".jpg", fileInputStream); // neuer Filename
                    httppost.setEntity(reqEntity);
                    Log.i(TAG, "request " + httppost.getRequestLine());
                    HttpResponse response = null;
                    try {
                        response = httpclient.execute(httppost);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        if (response != null)
                            Log.i(TAG, "response " + response.getStatusLine().toString());
                        StatusLine statusLine = response.getStatusLine();
                        serverResponseCode = statusLine.getStatusCode();
                    } finally {
                    }
                } finally {

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.i("Connection", "maybe no connection to server for file upload!!!");
            }
            return serverResponseCode;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer result) {
            // TODO Auto-generated method stub
            statusProgress.dismiss();
            super.onPostExecute(result);
            if (result == 200) {
                Toast.makeText(UploadPhotoActivity.this, "Success", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(UploadPhotoActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
            UploadPhotoActivity.this.finish();
        }
    }
}
