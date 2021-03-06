package de.eatgate.placessearch.deprecated;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.eatgate.placessearch.entities.EatGatePhotoUrl;

/**
 * Created by Shi on 30.03.2015.
 */
public class EatGatePhotoService {
    private final String server = "http://192.168.70.22/EatGate/api/WWWBewertungPortal?Service=ViewPhotoDownload";

    public EatGatePhotoService() {

    }

    /**
     * @param placeId
     * @return List of EatGatePhotoUrls from the Webservice Call
     * @throws Exception from WebService, JSON
     */
    public ArrayList<EatGatePhotoUrl> findEatGatePhotos(String placeId) throws Exception {

        String urlString = makeUrl(placeId);

        String json = getJSON(urlString);

        Log.i(" EatGateReview JSON ", json);

        JSONArray array = new JSONArray(json);

        ArrayList<EatGatePhotoUrl> arrayList = new ArrayList<EatGatePhotoUrl>();
        for (int i = 0; i < array.length(); i++) {
            EatGatePhotoUrl photoUrl = EatGatePhotoUrl.jsonToEatGatePhotoUrl((JSONObject) array.get(i));
            Log.i(" EatGatePhotoService ", photoUrl.toString());
            arrayList.add(photoUrl);
        }
        return arrayList;
    }

    /**
     * Builds the Url String for EatGateReview WebService
     *
     * @param placeId
     * @return
     */
    private String makeUrl(String placeId) {
        StringBuilder urlString = new StringBuilder(server);
        urlString.append("&Place_id=");
        urlString.append(placeId);
        return urlString.toString();
    }

    /**
     * Gets the JSON String
     *
     * @param url
     * @return JSON String
     * @throws Exception
     */
    protected String getJSON(String url) throws Exception {
        // makes the call
        return getUrlContents(url);
    }

    /**
     * Makes the call
     *
     * @param theUrl
     * @return
     * @throws Exception
     */
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
