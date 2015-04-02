package de.eatgate.placessearch.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import de.eatgate.placessearch.entities.Place;

/**
 *  Create request for Places API.

 *
 */
public class PlacesService {

    private final String TAG = "LOG_PLACESSERVICE";
    private String API_KEY;
    private String radius;
    private String types;
    private String searchWord;
    private String name;

	public PlacesService(String apikey, String radius, String types, String searchWord) {
        // Standard Suchparameter
        this.API_KEY = apikey;
        this.radius = radius;
        this.types = types;
        this.searchWord = "";
        this.name = null;
        // Debug Parameter
        if (searchWord.isEmpty() == false) {
            if (searchWord.startsWith("*")) {
                // radarsearch mit name
                this.name = null;
                this.radius = "5000.0";
                this.searchWord = searchWord.substring(1);
            } else {
                // radarsearch ohne name oder mit radius angabe
                try {
                    radius = "" + Double.parseDouble(searchWord);
                    this.radius = radius;
                    this.searchWord = "";
                } catch (NumberFormatException e) {
                    this.searchWord = "";
                    this.name = "";
                }
            }
        } else {
            // normale Suche
            this.searchWord = "";
            this.name = "";
        }
        Log.i(TAG, "Initialisiert " + this.types + ":" + this.searchWord);
    }

	public void setApiKey(String apikey) {
		this.API_KEY = apikey;
	}

    public ArrayList<Place> findPlaces(double latitude, double longitude) throws Exception {
        Log.i(TAG, "Bereite makeurl vor");
        String urlString = makeUrl(latitude, longitude);
        Log.i(TAG, "url:" + urlString);
        String json = getJSON(urlString);
        Log.i(TAG, "JSON: " + json);
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("results");
        ArrayList<Place> arrayList = new ArrayList<Place>();
        for (int i = 0; i < array.length(); i++) {
            try {
                Place place = Place
							.jsonToPlace((JSONObject) array.get(i));
					Log.v("Places Services ", "Place_id: " + place.getPlace_id());
					arrayList.add(place);
				} catch (Exception e) {
                Log.i(TAG, "Exception jsonToPlace: " + e.getMessage());
                throw new Exception(e.getMessage());
            }
			}
        return arrayList;
    }

	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(double latitude, double longitude) {
        // zum Test bestimmter Koord
        // latitude = 50.54335;
        // longitude = 9.675329;
        if (this.name == null) {
            StringBuilder urlString = new StringBuilder(
                    "https://maps.googleapis.com/maps/api/place/radarsearch/json?");
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&types=" + types);
            urlString.append("&rankby=distance");
            // urlString.append("&pagetoken");
            urlString.append("&radius=" + radius);
            // urlString.append("&country=Fulda");
            //urlString.append("&keyword=" + searchWord);
            urlString.append("&name=" + this.searchWord);
            urlString.append("&sensor=false&key=" + API_KEY);
            Log.i(TAG, "*" + urlString.toString());
            return urlString.toString();
        } else {
            StringBuilder urlString = new StringBuilder(
                    "https://maps.googleapis.com/maps/api/place/radarsearch/json?");
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&types=" + types);
            // urlString.append("&types=Frankfurt+am+Main");
            // urlString.append("&rankby=distance");
            // urlString.append("&pagetoken");
            urlString.append("&radius=" + radius);
            // urlString.append("&country=Fulda");
            urlString.append("&sensor=false&key=" + API_KEY);
            Log.i(TAG, "#" + urlString.toString());
            return urlString.toString();
        }
    }

	protected String getJSON(String url) {
		return getUrlContents(url);
	}

	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();

		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();
		} catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
		}
		return content.toString();
	}
}