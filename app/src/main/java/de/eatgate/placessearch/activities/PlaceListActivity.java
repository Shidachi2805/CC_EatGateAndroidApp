package de.eatgate.placessearch.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import de.eatgate.placessearch.R;
import de.eatgate.placessearch.entities.GPS;
import de.eatgate.placessearch.entities.Place;
import de.eatgate.placessearch.global.AppGob;
import de.eatgate.placessearch.helpers.InternetManager;
import de.eatgate.placessearch.helpers.ListViewAdapter;
import de.eatgate.placessearch.services.PlaceDetailsService;
import de.eatgate.placessearch.services.PlacesService;

public class PlaceListActivity extends Activity {
    private static final String API_KEY = "AIzaSyAWWG37dcyPNEQNvnP0b-S2-DZxCtKALBY";
    private final static String Str_aktuellePosition = "Hier bist Du";
    GPS gps;
    // Liste der gefundenen Orte, wird vom Asyn PlacesService mit Daten befuellt
    private ArrayList<Place> g_places = new ArrayList<Place>();
    // Details eines Ortes, wird vom Asyn PlaceDetailsService mit Daten befuellt
    private String types = "meal_takeaway|restaurant|meal_delivery";
    private String radius = "1000.0";
    private PlacesService placesService;
    private PlaceDetailsService placeDetailsService;
    private ListView placesListView;
    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);
        if (!InternetManager.isOnline(this)) {
            Toast.makeText(this, "Keine Internet-Verbindung", Toast.LENGTH_LONG).show();

        } else {
            gps = new GPS(this);
            try {
                gps.getLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // check if GPS location can get
            if (gps.canGetLocation()) {
                Log.d("Your Location", "latitude: " + gps.getLatitude() + ", longitude: " + gps.getLongitude());
            } else {
                // toDo Activity beenden oder andere Fehlerbehandlung
                // Can't get user's current location
                //  alert.showAlertDialog(MainActivity.this, "GPS Status",
                //          "Couldn't get location information. Please enable GPS",
                //          false);
                // stop executing code by return
                return;
            }
            // Call fuer Finden der Orte - radius, types
            new GetPlaces(PlaceListActivity.this, radius, types).execute();
        }
    }

    /**
     * Call Klasse zum Finden der Orte innerhalb des Radius und Types
     */
    private class GetPlaces extends AsyncTask<Void, Void, String> {

        private String types;
        private Context context;
        private String radius;

        public GetPlaces(Context context, String radius, String types) {
            this.context = context;
            this.types = types;
            this.radius = radius;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Liste der gefundenen Orte wird neu mit leerer Liste initialisiert
            g_places = new ArrayList<Place>();
            // creating PlacesService class object
            placesService = new PlacesService(API_KEY, radius, types);

        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(Void... arg0) {
            // Liste der Orte - Ergebnis von RadarSearch
            try {
                g_places = placesService.findPlaces(gps.getLatitude(), // 28.632808
                        gps.getLongitude()); // 77.218276
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Info", "Place_Anzahl: " + g_places.size());

            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {
            placesListView = (ListView) findViewById(R.id.listplaces);
            adapter = new ListViewAdapter(PlaceListActivity.this, g_places);
            placesListView.setAdapter(adapter);
            // Log.e("Places: ", g_places.get(0).getName());
        }
    }

    /**
     * Call der aufgefuehrt wird, wenn Details zu einem Ort abgefragt werden sollen
     */
    private class GetPlacesDetails extends AsyncTask<Void, Void, String> {

        private String places_id;
        private Context context;

        public GetPlacesDetails(Context context, String places_id) {
            this.context = context;
            this.places_id = places_id;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppGob app = (AppGob) getApplication();
            app.g_placeDetails = null;
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(Void... arg0) {
            // creating Places class object
            placeDetailsService = new PlaceDetailsService(API_KEY, places_id);
            AppGob app = (AppGob) getApplication();
            app.g_placeDetails = placeDetailsService.findPlaceDetails(); // 77.218276
            Log.e("GetPlacesDetails", " PlaceDetails: " + app.g_placeDetails.getName());
            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * *
         */
        protected void onPostExecute(String file_url) {

        }
    }
}
