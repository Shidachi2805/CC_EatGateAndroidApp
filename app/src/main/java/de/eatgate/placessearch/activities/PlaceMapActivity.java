package de.eatgate.placessearch.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

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
import de.eatgate.placessearch.services.PlaceDetailsService;
import de.eatgate.placessearch.services.PlacesService;


/**
 * Created by ProMarkt on 18.01.2015.
 */
public class PlaceMapActivity extends Activity implements OnMapReadyCallback {

    GPS gps;
    // Liste der gefundenen Orte, wird vom Asyn PlacesService mit Daten befuellt
    private ArrayList<Place> g_places = new ArrayList<Place>();
    // Details eines Ortes, wird vom Asyn PlaceDetailsService mit Daten befuellt
    // private PlaceDetails g_placeDetails;
    // Map speichert die Relation markerId, place_id;
    private HashMap<String,String> g_marker_id_place_id_map = new HashMap<String,String>();
    //  private String str_place_id = null;
    private Marker g_marker;
    private String types = "meal_takeaway|restaurant|meal_delivery";
    private String radius = "1000.0";
    private PlacesService placesService;
    private PlaceDetailsService placeDetailsService;
    private static final String API_KEY = "AIzaSyAWWG37dcyPNEQNvnP0b-S2-DZxCtKALBY";
    private GoogleMap g_meinGoogleMap;


    private final static String Str_aktuellePosition = "Hier bist Du";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_places);
        gps = new GPS(this);
        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude: " + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            // Can't get user's current location
            //  alert.showAlertDialog(MainActivity.this, "GPS Status",
            //          "Couldn't get location information. Please enable GPS",
            //          false);
            // stop executing code by return
            return;
        }
        // Call fuer Finden der Orte - radius, types
        new GetPlaces(PlaceMapActivity.this,radius,types).execute();

    }

    /* Wenn GoogleMap geladen, dann  ... */
    @Override
    public void onMapReady(GoogleMap map) {

        LatLng aktuellePosition = new LatLng(gps.getLatitude(), gps.getLongitude());

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(aktuellePosition, 13));

        map.addMarker(new MarkerOptions()
                .title(Str_aktuellePosition)
                .snippet("Deine aktuelle Position")
                .position(aktuellePosition));
        // fuer alle gefundenen Orte einen Marker in der Map setzen
        g_marker_id_place_id_map = new HashMap();
        for(Place p : g_places){
            Marker tmp_marker =
            map.addMarker(new MarkerOptions().
                    position(new LatLng(p.getLatitude(),p.getLongitude())).
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.mark_red)));
            g_marker_id_place_id_map.put(tmp_marker.getId(), p.getPlace_id());
            Log.e("HashMap: " ,"key" + tmp_marker.getId());
            Log.e("HashMap:", "value: " + p.getPlace_id());
        }
        g_meinGoogleMap = map;
        //   new GetPlacesDetails(PlaceMapActivity.this,g_marker.getTitle()).execute();
    }

    /**
     * Call Klasse zum Finden der Orte innerhalb des Radius und Types
     */
    private class GetPlaces extends AsyncTask<Void, Void, String> {

        private String types;
        private Context context;
        private String radius;

        public GetPlaces(Context context, String radius, String types){
            this.context = context;
            this.types = types;
            this.radius = radius;
        }
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Liste der gefundenen Orte wird neu mit leerer Liste initialisiert
            g_places = new ArrayList<Place>();
            // creating PlacesService class object
            placesService = new PlacesService(API_KEY,radius,types);

        }

        /**
         * getting Places JSON
         * */
        protected String doInBackground(Void... arg0) {

            g_places = placesService.findPlaces(gps.getLatitude(), // 28.632808
                    gps.getLongitude()); // 77.218276
            Log.e("Info", "Place_Anzahl: " + g_places.size());

            return "";
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         * **/
        protected void onPostExecute(String file_url) {
            Log.e("!!!!!","!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
             // wenn Call zum Finden der Orte zurueck ist dann wird die GoogleMap geladen
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            // ruft danach die OnMapReady auf
            mapFragment.getMapAsync(PlaceMapActivity.this);
            g_meinGoogleMap = mapFragment.getMap();
            // setzen des Infofensters, welches angezeigt wird, wenn auf einen Marker geklickt wird
            g_meinGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // bevor das InfoFenster erzeugt wird
                @Override
                public View getInfoWindow(Marker marker) {
                    if (marker.getTitle() != null && marker.getTitle().equals(Str_aktuellePosition)) {
                        return null;
                    }    // Exception einbauen fuer Keynotfound
                    String cur_place_id = g_marker_id_place_id_map.get(marker.getId());
                    // Aufruf der privaten Klasse  GetPlacesDetails, die den Service PlaceDetailsService aufruft
                    new GetPlacesDetails(PlaceMapActivity.this, cur_place_id).execute();
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    if (marker.getTitle() != null && marker.getTitle().equals(Str_aktuellePosition)) {
                        return null;
                    }
                    g_marker = marker;
                    // setzen des ClickListeners, wenn auf das Infofenster selbst geklickt wird
                    g_meinGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                                            @Override
                                                            public void onMapClick(LatLng latLng) {
                                                                Log.e("onMapClick", "Click me : yes");
                                                            }
                                                        }
                    );
                    LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    // Log.e("View_LayoutInflater", "view " + layoutInflater);
                    View view = layoutInflater.inflate(R.layout.mapinfolayout, null);
                    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

                    Log.e("GetPlaces_onPostExecute", "g_marker_Titel: " + g_marker.getTitle());
                    int counter = 0;
                    AppGob app = (AppGob) getApplication();
                    if (app == null) return null;
                    // Infofenster wird erst angezeigt, wenn der Webservice - Call geantwortet
                    // hat, nach 10*20 Millisekunden wird abgebrochen
                    while(app.g_placeDetails == null && counter<= 100) {
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        counter++;
                    }
                    if (app.g_placeDetails == null) {
                         return null;
                    }
                    else
                    {
                        TextView textView_infoName = (TextView) view.findViewById(R.id.info_name);
                        TextView textView_infoAdresse = (TextView)view.findViewById(R.id.info_adresse);
                        textView_infoName.setText(app.g_placeDetails.getName());
                        textView_infoAdresse.setText(app.g_placeDetails.getVicinity());
                        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar1);

                       // Log.e("Rating: ",""+g_placeDetails.getRating());
                        if(app.g_placeDetails.getArrRev()!=null) {

                            double ratitng_sum = 0;
                            int anzahl = 0;
                            for(int i = 0; i < app.g_placeDetails.getArrRev().size(); i++)
                            {
                                if(app.g_placeDetails.getArrRev().get(i).getRating() > 0)
                                {
                                    ratitng_sum = ratitng_sum + app.g_placeDetails.getArrRev().get(i).getRating();
                                    anzahl++;
                                }
                            }
                            // Setze Rating in g_placeDetails auf Durchschnittswertung
                            if (anzahl != 0)
                            {
                                ratingBar.setRating((float) ratitng_sum/anzahl);
                                app.g_placeDetails.setRating(ratitng_sum/anzahl);
                            }
                            else
                            {
                                ratingBar.setRating(0);
                                app.g_placeDetails.setRating(0);
                            }
                        }
                    }
                    return view;
                }
            });
        }
    }

    /**
     * Call der aufgefuehrt wird, wenn Details zu einem Ort abgefragt werden sollen
     */
    private class GetPlacesDetails extends AsyncTask<Void, Void, String> {

        private String places_id;
        private Context context;

        public GetPlacesDetails(Context context, String places_id){
            this.context = context;
            this.places_id = places_id;
        }
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppGob app = (AppGob) getApplication();
            app.g_placeDetails = null;
            g_marker = null;
        }

        /**
         * getting Places JSON
         * */
        protected String doInBackground(Void... arg0) {
            // creating Places class object
            placeDetailsService = new PlaceDetailsService(API_KEY,places_id);
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
         * **/
        protected void onPostExecute(String file_url) {
            g_meinGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                @Override
                public void onInfoWindowClick(Marker marker) {
                    if (marker == null) {
                        Log.e("MapActivity: ", "Marker ist null");
                    } else {
                        Log.e("MapActivity: ", "Marker ist nicht null");
                        //str_place_id = marker.getTitle();
                    }
                    AppGob app = (AppGob) getApplication();
                    if (app != null && !g_marker.getTitle().equals(Str_aktuellePosition) && app.g_placeDetails != null)
                    {
                        // Starten einer neuen Activity, welches dies PlaceDetails anzeigt
                        Intent intent = new Intent(PlaceMapActivity.this, SinglePlacesActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
            AppGob app = (AppGob) getApplication();
            if(app != null && app.g_placeDetails != null) {
                g_marker.setTitle(app.g_placeDetails.getName());
            }
        }
    }

}
