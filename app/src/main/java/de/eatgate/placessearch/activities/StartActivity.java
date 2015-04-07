package de.eatgate.placessearch.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private final static String TVLOC = "Ort eingeben";//< Ort eingeben Bsp. Berlin >";
    private final static String TAG = "LOG_STARTACTIVITY";
    private final static String TVTXT = "Suche eingeben"; //< Suche eingeben Bsp. Pizza >";
    private static GPSManager gpsManager;
    private TextView textViewGPS;
    private Button btnToMapActivity;
    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;
    private Button btnToListActivity;
    private HashMap<String, Koord> koordinates;
    // private final static String TXT = "TEXTSUCHE";
    // private final static String LOCATION = "STANDORTSUCHE";
    private List<String> names;

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
        fillGeoLocations();
        AutoCompleteTextView tv = (AutoCompleteTextView) findViewById(R.id.search_word);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView tv = (AutoCompleteTextView) v;
                if (tv.getText().toString().contains(TVTXT)) {
                    tv.setText("");
                }
            }
        });
        tv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                AutoCompleteTextView tv = (AutoCompleteTextView) v;
                if (tv.getText().toString().contains(TVTXT)) {
                    tv.setText("");
                }
                return false;
            }
        });

        // toDo User Login : fuer ersten Release ist der User immer als User mit Id 1 eingeloggt
        AppGob app = (AppGob) getApplication();
        app.mUserId = 1;

        textViewGPS = (TextView) findViewById(R.id.textViewGPS);
        gpsManager = new GPSManager(this, textViewGPS);
        gpsManager.checkCpsConNw(); // prueft ob Position des Devices bestimmt werden kann
        // und setzt das Textfeld in der View mit dem Ort

        // Standortsuche
        btnToMapActivity = (Button) findViewById(R.id.btnStartToMap);
        btnToMapActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bInternet = InternetManager.isOnline(StartActivity.this);
                boolean bGPS = gpsManager.isGPS();
                if (bInternet) {
                    TextView tv1 = (TextView) (StartActivity.this).findViewById(R.id.search_word);
                    TextView tv2 = (TextView) (StartActivity.this).findViewById(R.id.autoCompleteTextView);
                    if (tv1.getText().toString().contains(TVTXT)) {
                        tv1.setText("");
                    }
                    if (tv2.getText().toString().contains(TVLOC)) {
                        tv2.setText("");
                    }
                    String search_word = tv1.getText().toString().trim();
                    String location_word = tv2.getText().toString().trim();
                    // nur wenn Internetverbindung vorhanden, kann die Map gestartet werden
                    Intent intent = new Intent(StartActivity.this, PlaceMapActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.putExtra("flag",LOCATION);
                    if (location_word != null && !location_word.isEmpty()) {
                        Koord koordinate = koordinates.get(location_word);
                        if (koordinate != null) {
                            intent.putExtra("lng", koordinate.getLng());
                            intent.putExtra("lat", koordinate.getLat());
                        } else {
                            // nichts gefunden
                            intent.putExtra("lng", 0.0);
                            intent.putExtra("lat", 0.0);
                            intent.putExtra("location_word", "");
                        }

                    }
                    Log.i(TAG, "Locationword:" + location_word);
                    intent.putExtra("search_word", search_word);
                    intent.putExtra("location_word", location_word);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(StartActivity.this, " Keine Internetverbindung !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        // prüft ob Internetverbindung verfügbar
        if (!InternetManager.isOnline(this)) {
            Toast.makeText(this,
                    " Keine Internetverbindung ! ", Toast.LENGTH_SHORT).show();
        }
        // Textsuche
        btnToListActivity = (Button) findViewById(R.id.btnStartToList);
        btnToListActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean bInternet = InternetManager.isOnline(StartActivity.this);
                boolean bGPS = gpsManager.isGPS();
                if (bInternet) {
                    TextView tv1 = (TextView) (StartActivity.this).findViewById(R.id.search_word);
                    TextView tv2 = (TextView) (StartActivity.this).findViewById(R.id.autoCompleteTextView);
                    String search_word = tv1.getText().toString().trim();
                    String location_word = tv2.getText().toString().trim();
                    Intent intent = new Intent(StartActivity.this, PlaceListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // intent.putExtra("flag", TXT);
                    intent.putExtra("location_word", location_word);
                    intent.putExtra("search_word", search_word);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(StartActivity.this, " Keine Internetverbindung !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    /**
     * TODO Read from Database
     */
    private void initGeoLocations() {
        names = new ArrayList<String>();
        names.add("Fulda");
        names.add("Frankfurt");
        names.add("Berlin");
        names.add("Würzburg");
        names.add("Hamburg");
        names.add("Hanau");
        // latitude = 50.54335;
        // longitude = 9.675329;
        koordinates = new HashMap<String, Koord>();
        koordinates.put(names.get(0), new Koord(9.675329, 50.54335));
        koordinates.put(names.get(1), new Koord(8.6799935, 50.112857));
        koordinates.put(names.get(2), new Koord(13.369423, 52.525348));
        koordinates.put(names.get(3), new Koord(9.935791, 49.801938));
        koordinates.put(names.get(4), new Koord(10.006164, 53.553485));
        koordinates.put(names.get(5), new Koord(8.929021, 50.121177));
    }

    public void fillGeoLocations() {
        initGeoLocations();
        AutoCompleteTextView autoTV = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoTV.setAdapter(new ListViewAdapter_Locs(StartActivity.this.getApplicationContext(), names));
        autoTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView tv = (AutoCompleteTextView) v;
                if (tv.getText().toString().contains(TVLOC)) {
                    tv.setText("");
                }
            }
        });
        autoTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                AutoCompleteTextView tv = (AutoCompleteTextView) v;
                if (tv.getText().toString().contains(TVLOC)) {
                    tv.setText("");
                }
                return false;
            }
        });
        //  mKontaktAdapter =
        //          new ArrayAdapter<String>(this,
        //                  android.R.layout.simple_list_item_1, NAMES); // (4)
        //  setListAdapter(mKontaktAdapter); // (5)
        //   listAdapter = new ArrayAdapter<String>(this,R.layout.simplerow,arrList);
        //   listView_we_day.setAdapter(listAdapter);
    }

    /**
     * Einfacher Dialogbuilder fuer die InfoBox
     */

    class Koord {
        private double lng = 0;
        private double lat = 0;

        public Koord(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }

    public class ListViewAdapter_Locs extends ArrayAdapter<String> {
        private Context context;
        private List<String> tag = new ArrayList<String>();

        public ListViewAdapter_Locs(Context context, List<String> tag) {
            super(context, R.layout.simplerow, tag);
            this.context = context;
            this.tag = tag;
        }
    }

// class End
}
