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

	private String API_KEY;
    private String radius;
    private String types;

	public PlacesService(String apikey, String radius, String types) {
        this.API_KEY = apikey;
        this.radius = radius;
        this.types = types;
	}

	public void setApiKey(String apikey) {
		this.API_KEY = apikey;
	}

    public ArrayList<Place> findPlaces(double latitude, double longitude) throws Exception {

        String urlString = makeUrl(latitude, longitude);

			String json = getJSON(urlString);

			System.out.println(json);
			JSONObject object = new JSONObject(json);
			JSONArray array = object.getJSONArray("results");

			ArrayList<Place> arrayList = new ArrayList<Place>();
			for (int i = 0; i < array.length(); i++) {

                Place place = Place
							.jsonToPlace((JSONObject) array.get(i));
                Log.i("Places Services ", "Place_id: " + place.getPlace_id());
                arrayList.add(place);

            }
			return arrayList;
	}

	// https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
	private String makeUrl(double latitude, double longitude) {
		StringBuilder urlString = new StringBuilder(
				"https://maps.googleapis.com/maps/api/place/radarsearch/json?");
        //latitude = 50.55162;
        //longitude = 9.67518;
        if (types.equals("")) {
			urlString.append("&location=");
			urlString.append(Double.toString(latitude));
			urlString.append(",");
			urlString.append(Double.toString(longitude));
			urlString.append("&radius=" + radius);
			//urlString.append("&types="+ types);
			urlString.append("&sensor=true&key=" + API_KEY);
		} else {
            // zum Test bestimmter Koord im Emulator
            // telnet localhost 5554
            // geo fix long lat
            //  latitude = 50.55162;
            //  longitude = 9.67518;
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

           // urlString.append("&name=sushi");
			urlString.append("&sensor=true&key=" + API_KEY);
		}
		return urlString.toString();
	}

    protected String getJSON(String url) throws Exception {
        return getUrlContents(url);
	}

    private String getUrlContents(String theUrl) throws Exception {
        StringBuilder content = new StringBuilder();

			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line + "\n");
			}
			bufferedReader.close();

        return content.toString();
    }
}