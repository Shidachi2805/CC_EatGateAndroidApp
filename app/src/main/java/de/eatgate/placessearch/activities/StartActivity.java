package de.eatgate.placessearch.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.global.AppGob;
import de.eatgate.placessearch.helpers.GPSManager;
import de.eatgate.placessearch.helpers.InternetManager;

/**
 * Einstiegspunkt der App
 * Features: Standortsuche, Ortssuche
 * toDo Ortssuche noch nicht implementiert
 * toDo Auswahl von Rubriken, Auswahl des Radius
 */
public class StartActivity extends Activity {
    private static GPSManager gpsManager;
    private TextView textViewGPS;
    private Button btnToMapActivity;
    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;

    /**
     * Einfacher Dialogbuilder fuer die InfoBox
     */
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
        setContentView(R.layout.activity_start);
        buildInfoDialog();

        // toDo User Login : fuer ersten Release ist der User immer als User mit Id 1 eingeloggt
        AppGob app = (AppGob) getApplication();
        app.mUserId = 1;

        textViewGPS = (TextView) findViewById(R.id.textViewGPS);
        gpsManager = new GPSManager(this, textViewGPS);
        gpsManager.checkCpsConNw(); // prueft ob Position des Devices bestimmt werden kann
        // und setzt das Textfeld in der View mit dem Ort
        btnToMapActivity = (Button) findViewById(R.id.btnToMap);
        btnToMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bInternet = InternetManager.isOnline(StartActivity.this);
                boolean bGPS = gpsManager.isGPS();
                if (bInternet) {
                    TextView tv = (TextView) (StartActivity.this).findViewById(R.id.search_word);
                    String search_word = tv.getText().toString();
                    // nur wenn Internetverbindung vorhanden, kann die Map gestartet werden
                    Intent intent = new Intent(StartActivity.this, PlaceMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("search_word", search_word);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(StartActivity.this, " Keine Internetverbindung !",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        // macht den Nutzer beim Start aufmerksam, das Device im Moment keine Internetverbindung hat
        if (!InternetManager.isOnline(this)) {
            Toast.makeText(this,
                    " Keine Internetverbindung ! ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    /**
     * Erzeugt die Actionbar, das Hauptmenu der Anwendung
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ActionBar bar = getActionBar(); // oder MainActivity.getInstance().getActionBar()
        TextView titleView = (TextView) findViewById(R.id.action_bar_title);
        if (titleView == null) {
            int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
            titleView = (TextView) findViewById(titleId);
        }
        titleView.setText("EatGate");
        titleView.setTextColor(Color.WHITE);
        bar.setDisplayShowTitleEnabled(false);  // erzwingt neuzeichnen
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_close) {
            finish();
            return true;
        }
        if (id == R.id.action_info) {
            dialog.show();
            return true;
        }
        if (id == R.id.action_menu) {
            onClickMainMenu(menuItemView);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Für ein Contextmenu um auf Klicks zu reagieren - nicht benutzt
     *
     * @param item
     * @return
     */
    public boolean onContextItemSelected(MenuItem item) {
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo)
                item.getMenuInfo();
        View kontext = info.targetView;
        switch (item.getItemId()) {
            case R.id.action_info:
                dialog.show();
                return true;
            case R.id.action_close:
                finish();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
                    case R.id.action_gps:
                        gpsManager.checkGpsConDialog();
                        return true;
                    case R.id.action_info:
                        dialog.show();
                        return true;
                    case R.id.action_close:
                        finish();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

// class End
}
