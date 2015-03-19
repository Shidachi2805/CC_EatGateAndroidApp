package de.eatgate.placessearch.deprecated;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import de.eatgate.placessearch.global.AppGob;

/**
 * Created by Shi on 31.03.2015.
 */
public class EatGatePlaceService {
    private final String server = "http://192.168.70.22/EatGate/api/WWWBewertungPortal";
    private String placeId;

    public EatGatePlaceService(String placeId) {
        this.placeId = placeId;
    }


    public boolean checkEatGatePlace(String placeId, AppGob appGob) throws Exception {

        String urlString = makeUrl(placeId, appGob.g_placeDetails.getName(),
                appGob.g_placeDetails.getVicinity());

        String json = getJSON(urlString); // call to server

        Log.i(" chekEatGatePlace ", json);
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("results");

        return true;
    }

    private String makeUrl(String placeId, String name, String addr) throws UnsupportedEncodingException {
        StringBuilder urlString = new StringBuilder(server);
        urlString.append("&Place_id=");
        urlString.append(URLEncoder.encode(placeId, "UTF-8"));
        urlString.append("&Name=");
        urlString.append(URLEncoder.encode(name, "UTF-8"));
        urlString.append("&Adresse=");
        urlString.append(URLEncoder.encode(addr, "UTF-8"));
        urlString.append("&Lat=0&Lng=0"); // dummy not used yet
        return urlString.toString();
    }

    /**
     * Call to EatGate Server
     *
     * @param url
     * @return
     * @throws Exception
     */
    protected String getJSON(String url) throws Exception {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) throws Exception {
        StringBuilder content = new StringBuilder();
        URL url = new URL(theUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
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
