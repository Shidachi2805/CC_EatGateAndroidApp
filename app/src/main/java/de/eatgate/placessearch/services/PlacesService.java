package de.eatgate.placessearch.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.eatgate.placessearch.entities.Place;

/**
 *  Create request for Places API.

 *
 */
public class PlacesService {

	private String API_KEY;
    private String radius;
    private String types;
    private String searchWord;

	public PlacesService(String apikey, String radius, String types, String searchWord) {
        // Standard Suchparameter
        this.API_KEY = apikey;
        this.radius = radius;
        this.types = types;
        this.searchWord = "";
        // Debug Parameter
        if(!searchWord.isEmpty()) {
            try {
               radius = "" + Double.parseDouble(searchWord);
               this.radius = radius;
               this.searchWord = "";
            } catch (NumberFormatException e)  {
                searchWord = searchWord.trim();
                if(searchWord.startsWith("#")) {
                    this.types=searchWord.substring(1);
                    this.searchWord = "";
                } else {
                    this.searchWord = searchWord;
                }
            }
        } else {
            this.searchWord = "";
        }
	}

	public void setApiKey(String apikey) {
		this.API_KEY = apikey;
	}

	public ArrayList<Place> findPlaces(double latitude, double longitude) {

		String urlString = makeUrl(latitude, longitude);

		try {
			String json = getJSON(urlString);

			System.out.println(json);
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

				}
			}
			return arrayList;
		} catch (JSONException ex) {
		        Logger.getLogger(PlacesService.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return null;
	}

	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(double latitude, double longitude) {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/radarsearch/json?");

		if (types.equals("")) {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
			urlString.append("&radius=" + radius);
            if(!searchWord.isEmpty()) {
                urlString.append("&name=" + searchWord);
            }
			urlString.append("&sensor=true&key=" + API_KEY);
		} else {
            // zum Test bestimmter Koord
            //latitude = 50.54335;
            //longitude = 9.675329;
            urlString.append("location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&types="+types);
            //urlString.append("&types=Frankfurt+am+Main");
            //urlString.append("&rankby=distance");
            urlString.append("&pagetoken");
            urlString.append("&radius=" + radius);
            // urlString.append("&country=Fulda");
            if(!searchWord.isEmpty()) {
                urlString.append("&name=" + searchWord);
            }
			urlString.append("&sensor=true&key=" + API_KEY);
		}
		return urlString.toString();
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
            Log.e("Catch me, ", "if you can!");
			e.printStackTrace();
		}
		return content.toString();
	}
}