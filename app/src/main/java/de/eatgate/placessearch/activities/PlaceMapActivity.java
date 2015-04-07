package de.eatgate.placessearch.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.entities.GPS;
import de.eatgate.placessearch.entities.Place;
import de.eatgate.placessearch.entities.PlaceDetails;
import de.eatgate.placessearch.global.AppGob;
import de.eatgate.placessearch.helpers.InternetManager;
import de.eatgate.placessearch.services.PlaceDetailsService;
import de.eatgate.placessearch.services.PlacesService;


/**
 * Created by ProMarkt on 18.01.2015.
 */
public class PlaceMapActivity extends Activity implements OnMapReadyCallback {

    private static final String API_KEY = "AIzaSyAWWG37dcyPNEQNvnP0b-S2-DZxCtKALBY";
    private final static String STR_AKTUELLEPOSITION = "Hier bist Du";
    private final static String STR_LOCATION = "Startpunkt";
    private final String TAG = "LOG_PLACEMAPACTIVITY";
    private final String YOURPOSTXT = "Deine aktuelle Position";
    private final int ZOOMFAKTOR = 13;
    private GPS gps;
    // Liste der gefundenen Orte, wird vom Asyn PlacesService mit Daten befuellt
    private ArrayList<Place> allFoundPlaces = null;
    // Map speichert die Relation markerId, place_id;
    private HashMap<String, String> marker_id_place_id_map = null;
    private Marker markerOpenInfoWindow = null;
    private String types = "meal_takeaway|restaurant|meal_delivery";
    private String short_radius = "1200.0";
    private String long_radius = "5000.0";
    private String radius = "1200.0";
    private String search_word = "";
    private String location_word = "";
    private PlacesService placesService = null;
    private PlaceDetailsService placeDetailsService = null;
    private GoogleMap meinGoogleMap = null;
    private ProgressDialog statusProgress = null;
    private AlertDialog.Builder builder = null;
    private AlertDialog dialog = null;
    private AppGob appGob = null;
    private double lng = 0;
    private double lat = 0;
    // private final static String TXT = "TEXTSUCHE";
    // private final static String LOCATION = "STANDORTSUCHE";

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

    private void getSearchOptions(Bundle b) {
        // Resources res = getResources();
        // String text = String.format(res.getString(R.string.searchLocTxt));
        Log.i(TAG, "Getting Extras ...");
        if (b != null) {
            if ((b.get("location_word") == null)) {
                location_word = "";
            } else {
                location_word = b.get("location_word").toString().trim().toLowerCase();
            }
            if ((b.get("search_word") == null)) {
                search_word = "";
            } else {
                search_word = b.get("search_word").toString().trim().toLowerCase();
            }
            if (b.get("lng") != null) {
                double lg = (double) (b.get("lng"));
                if (lg != 0.0) {
                    lng = lg;
                }
            } else lng = 0.0;
            if (b.get("lat") != null) {
                double la = (double) (b.get("lat"));
                if ((la != 0.0)) {
                    lat = la;
                }
            } else lat = 0.0;
        } else {
            location_word = "";
            search_word = "";
        }
        Log.i(TAG, "Got Extras ...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_places);
        marker_id_place_id_map = null;
        markerOpenInfoWindow = null;
        allFoundPlaces = null;
        meinGoogleMap = null;
        appGob = (AppGob) getApplication();
        if (appGob == null) {
            Toast.makeText(this, "Undefinierter Fehler", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        Bundle b = getIntent().getExtras();
        getSearchOptions(b);

        buildInfoDialog();
        if (!InternetManager.isOnline(this)) {
            Toast.makeText(this, "Keine Internet-Verbindung", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            gps = new GPS(this);
            try {
                gps.getLocation();
                // check if GPS location can get
                if (gps.canGetLocation()) {
                    Log.i(TAG, "Your Position in latitude: " + gps.getLatitude() + " : longitude: " + gps.getLongitude());
                    statusProgress = ProgressDialog.show(this, "Bitte warten ...", "Google Maps wird geladen ...",
                            true, false);
                    // Call fuer das Finden der Orte - radius, types, name
                    if (location_word.isEmpty()) {
                        new GetPlaces(short_radius, types, search_word, gps.getLongitude(),
                                gps.getLatitude()).execute();
                    } else {
                        // toDo auslesen von lng lat
                        // latitude = 50.54335;
                        // longitude = 9.675329;
                        new GetPlaces(long_radius, types, search_word, lng, lat).execute();
                    }
                } else {
                    Toast.makeText(this, "Keine Positionsbestimmung möglich. Schalten Sie die Standortbestimmung ein!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception ex) {
                Log.e(TAG, "Fehler im GPS Modul!");
                statusProgress.dismiss();
                Toast.makeText(this, "Fehler im GPS Modul!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
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

    /**
     * Wenn Google Map Ready, dann
     *
     * @param map
     * @see PlaceMapActivity.GetPlaces onPostExecute
     */
    @Override
    public void onMapReady(GoogleMap map) {
        Log.i(TAG, "onMapReady wurde aufgerufen!");
        if (map == null) {
            Log.e(TAG, "Map is null");
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return;
        }
        // Positon wird aus der GPS-Klasse ermittelt, dafuer muss eine Positionsbestimmung
        // moeglich sein, ansonsten beendet die Activity und kehrt zur StartActivity zurueck
        // dies wird in der OnCreate geprueft
        if (lat != 0 && lng != 0) {
            LatLng aktuellePosition = new LatLng(lat, lng);
            Log.i(TAG, "Your location for MAPS: " + gps.getLatitude() + ":" + gps.getLongitude());
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktuellePosition, ZOOMFAKTOR));
            map.addMarker(new MarkerOptions()
                    .title(STR_LOCATION)
                    .snippet(STR_LOCATION)
                    .position(aktuellePosition));
        } else {
            LatLng aktuellePosition = new LatLng(gps.getLatitude(), gps.getLongitude());
            Log.i(TAG, "Your location for MAPS: " + gps.getLatitude() + ":" + gps.getLongitude());
            map.setMyLocationEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktuellePosition, ZOOMFAKTOR));
            map.addMarker(new MarkerOptions()
                    .title(STR_LOCATION)
                    .snippet(YOURPOSTXT)
                    .position(aktuellePosition));
            // fuer alle gefundenen Orte einen Marker in der Map setzen
        }
        marker_id_place_id_map = new HashMap();
        if (allFoundPlaces == null || allFoundPlaces.isEmpty()) {
            Log.i(TAG, "Keine Locations gefunden!");
        } else {
            // fuer jeden gefunden Ort einen Marker in der Map setzen
            // und in einer Marker-HashMap speichern
            for (Place p : allFoundPlaces) {
                Marker tmp_marker =
                        map.addMarker(new MarkerOptions().
                                position(new LatLng(p.getLatitude(), p.getLongitude())).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red)));
                marker_id_place_id_map.put(tmp_marker.getId(), p.getPlace_id());
                Log.i(TAG, "HashMap:  key" + tmp_marker.getId());
                Log.i(TAG, "HashMap: value: " + p.getPlace_id());
            }
        }
        setMeinGoogleMap(map);
    }

    private void setMeinGoogleMap(GoogleMap map) {
        meinGoogleMap = map;
        Log.i(TAG, "setInfoWindowAdpater fuer GoogleMap!");
        meinGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            private PlaceDetails placeDetails = null;

            // bevor das InfoFenster erzeugt wird
            @Override
            public View getInfoWindow(Marker marker) {
                if (marker.getTitle() != null && (marker.getTitle().equals(STR_AKTUELLEPOSITION) || marker.getTitle().equals(STR_LOCATION))) {
                    // Info Fenster wird nicht angezeigt
                    Log.i(TAG, "getInfoWindow Marker ist null oder STR_AKTUELLEPOSITION");
                    return null;
                }    // toDo Exception einbauen fuer Keynotfound
                String cur_place_id = marker_id_place_id_map.get(marker.getId());
                // Aufruf der privaten Klasse  GetPlacesDetails, die den Service PlaceDetailsService aufruft
                Log.i(TAG, "GetPlacesDetails wird aufgerufen mit Place_id " + cur_place_id);
                new GetPlacesDetails(cur_place_id).execute();
                Log.i(TAG, "GetPlacesDetails wurde aufgerufen");
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTitle() != null && (marker.getTitle().equals(STR_AKTUELLEPOSITION) || marker.getTitle().equals(STR_LOCATION))) {
                    // Info Fenster wird nicht angezeigt
                    return null;
                }
                markerOpenInfoWindow = marker;
                // setzen des ClickListeners, wenn auf das Infofenster selbst geklickt wird
                meinGoogleMap.setOnMapClickListener(
                        new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                Log.i(TAG, "Map Location Info Window is open now!");
                            }
                        }
                );
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                View view = layoutInflater.inflate(R.layout.mapinfolayout, null);
                view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

                Log.i(TAG, "markerOpenInfoWindow: Titel ist " + markerOpenInfoWindow.getTitle());
                int counter = 0;
                appGob = (AppGob) getApplication();

                if (appGob == null) return null;
                // TODO onPostExecute
                // Infofenster wird erst angezeigt, wenn der Webservice - Call geantwortet
                // hat, nach 100*50 Millisekunden Timeout
                while (placeDetails == null && counter <= 300) {
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        Log.e(TAG, "Interrupt Exception");
                        Toast.makeText(PlaceMapActivity.this, "Error Thread Exception!",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlaceMapActivity.this, StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    counter++;
                }
                if (placeDetails == null) {
                    Log.e(TAG, "placeDetails ist null!");
                    Toast.makeText(PlaceMapActivity.this, "Server no response, try again!", Toast.LENGTH_SHORT).show();

                } else {
                    TextView textView_infoName = (TextView) view.findViewById(R.id.info_name);
                    TextView textView_infoAdresse = (TextView) view.findViewById(R.id.info_adresse);
                    textView_infoName.setText(appGob.g_placeDetails.getName());
                    textView_infoAdresse.setText(appGob.g_placeDetails.getVicinity());
                    RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar1);
                    if (appGob.g_placeDetails.getArrRev() != null) {
                        double ratitng_sum = 0;
                        int anzahl = 0;
                        for (int i = 0; i < appGob.g_placeDetails.getArrRev().size(); i++) {
                            if (appGob.g_placeDetails.getArrRev().get(i).getRating() > 0) {
                                ratitng_sum = ratitng_sum + appGob.g_placeDetails.getArrRev().get(i).getRating();
                                anzahl++;
                            }
                        }
                        // Setze Rating in g_placeDetails auf Durchschnittswertung
                        if (anzahl != 0) {
                            ratingBar.setRating((float) ratitng_sum / anzahl);
                            appGob.g_placeDetails.setRating(ratitng_sum / anzahl);
                        } else {
                            ratingBar.setRating(0);
                            appGob.g_placeDetails.setRating(0);
                        }
                    }
                }
                return view;
            }

            /**
             * Call der aufgefuehrt wird, wenn Details zu einem Ort abgefragt werden sollen
             * <Params,Progress,Rueckgabewert>
             */
            class GetPlacesDetails extends AsyncTask<Void, Integer, Integer> {
                private String id;

                public GetPlacesDetails(String id) {
                    this.id = id;
                }

                /**
                 * Before starting background thread Show Progress Dialog
                 */
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    placeDetails = null;  // sicherstellen, dass Objekt vor Call null ist
                    appGob.g_placeDetails = null;
                    markerOpenInfoWindow = null;
                }

                /**
                 * getting Places JSON
                 */
                protected Integer doInBackground(Void... arg0) {
                    // creating Places class object
                    try {
                        Log.i(TAG, "Before Call placeDetailsService");
                        placeDetailsService = new PlaceDetailsService(API_KEY, id);
                        placeDetails = placeDetailsService.findPlaceDetails();
                        appGob.g_placeDetails = placeDetails; // TODO removing after complete refactoring
                        Log.i(TAG, placeDetails.toString());
                        return 201;
                    } catch (Exception e) {
                        Log.e(TAG, "Webservice Exception");
                        statusProgress.dismiss();
                        Toast.makeText(PlaceMapActivity.this, "Fehler! Wahrscheinlich Probleme " +
                                "mit der Internetverbindung!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PlaceMapActivity.this, StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        return 404;
                    }
                }

                @Override
                protected void onProgressUpdate(Integer... values) {

                }

                /**
                 * After completing background task Dismiss the progress dialog
                 * and show the data in UI
                 * Always use runOnUiThread(new Runnable()) to update UI from background
                 * thread, otherwise you will get error
                 * *
                 */
                @Override
                protected void onPostExecute(Integer code) {
                    meinGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if (marker == null) {
                                Log.e(TAG, "Marker ist null");
                            } else {
                                Log.i(TAG, "Marker ist nicht null");
                            }
                            if (appGob != null && !markerOpenInfoWindow.getTitle().equals
                                    (STR_AKTUELLEPOSITION) && appGob.g_placeDetails != null) {
                                // Starten einer neuen Activity, welches dies PlaceDetails anzeigt
                                Intent intent = new Intent(PlaceMapActivity.this, SinglePlacesActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                // finish();
                            }
                        }
                    });
                    if (appGob != null && appGob.g_placeDetails != null) {
                        markerOpenInfoWindow.setTitle(appGob.g_placeDetails.getName());
                    }
                }
            }
        });
    }


    /**
     * Reagiert auf Clicks in der Actionbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        View menuItemView = findViewById(R.id.action_menu);
        if (id == R.id.action_menu) {
            onClickMainMenu(menuItemView);
        } else if (id == R.id.action_back) {
            Intent intent = new Intent(this, StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Erzeugt das Popup Menu und registriert den ClickListener fuer das Submenu
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
     * Call Klasse zum Finden der Orte innerhalb des Radius und Types
     */
    private class GetPlaces extends AsyncTask<Void, Void, String> {

        private String types;
        private String radius;
        private String searchWord;
        private double longitude;
        private double latitude;

        public GetPlaces(String radius, String types, String searchWord,
                double longitude, double latitude)
        {
            this.types = types;
            this.radius = radius;
            this.searchWord = searchWord;
            this.longitude = longitude;
            this.latitude = latitude;
            Log.i(TAG, "Initialisiere GetPlaces: " + types + ":" + radius + ":" + searchWord + "!");
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            Log.i(TAG, "OnPreEexecute breitet GetPlaces Call vor!");
            // Liste der gefundenen Orte wird neu mit leerer Liste initialisiert
            allFoundPlaces = new ArrayList<Place>();
            // creating PlacesService class object
            placesService = new PlacesService(API_KEY, radius, types, searchWord,longitude,latitude);
            Log.i(TAG, "OnPreEexecute hat GetPlaces Call vorbereitet!");
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(Void... arg0) {
            try {
                Log.i(TAG, "PlacesService findPlaces wird aufgerufen!");
                allFoundPlaces = placesService.findPlaces();
                Log.i(TAG, "PlacesService findPlaces hat Ergebnis geliefert! ");
            } catch (Exception e) {
                Log.e(TAG, "PlacesService verursacht Fehler!" + e.getMessage());
                Intent intent = new Intent(PlaceMapActivity.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return "";
            }
            Log.i(TAG, "Place_Anzahl: " + allFoundPlaces.size());
            return "";
        }

        @Override
        protected void onPostExecute(String file_url) {
            statusProgress.dismiss();
            try {
                // wenn Call zum Finden der Orte zurueck ist, dann wird die GoogleMap geladen
                MapFragment mapFragment = (MapFragment) PlaceMapActivity.this.getFragmentManager()
                        .findFragmentById(R.id.map);
                // ruft danach die OnMapReady auf
                Log.i(TAG, "getMapAsync wird jetzt aufgerufen!");
                mapFragment.getMapAsync(PlaceMapActivity.this);
                Log.i(TAG, "getMapAsync wurde aufgerufen!");

                Log.i(TAG, "mapFragment wurde aufgerufen!");
                // setzen des Infofensters, welches angezeigt wird, wenn auf einen Marker geklickt wird

            } catch (Exception e) {
                Toast.makeText(PlaceMapActivity.this, "Fehler! Wahrscheinlich Google Play Dienste nicht installiert!", Toast.LENGTH_LONG).show();
                statusProgress.dismiss();
                Intent intent = new Intent(PlaceMapActivity.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }
    }
}
