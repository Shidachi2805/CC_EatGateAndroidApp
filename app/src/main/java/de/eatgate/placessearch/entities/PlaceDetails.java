package de.eatgate.placessearch.entities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ProMarkt on 19.01.2015.
 */
public class PlaceDetails {
    private double rating;
    private String icon;
    private String place_id;
    private String name;
    private String formatted_address;
    private String vicinity;
    private String openhours;
    private ArrayList<String> weekdays;
    private ArrayList<Review> arrRev;

    /**
     * liefert das Objekt
     *
     * @param jsonObject
     * @return
     */
    public static PlaceDetails jsonToPlaceDetails(JSONObject jsonObject) {
        try {
            PlaceDetails result = new PlaceDetails();
            ArrayList<Review> arrReviews = new ArrayList<Review>();
            if (!jsonObject.isNull("reviews")) {
                JSONArray reviewsArr = (JSONArray) jsonObject.get("reviews");
                for (int i = 0; i < reviewsArr.length(); i++) {
                    try {
                        Review rv = Review.jsonToReview((JSONObject) reviewsArr.get(i));
                        arrReviews.add(rv);
                    } catch (Exception e) {
                        Log.e("JSONObj, ", "kein Review Array");
                    }
                }
                result.setArrRev(arrReviews);
            }
            // toDo isNull einfuegen
            result.setIcon(jsonObject.getString("icon"));
            result.setName(jsonObject.getString("name"));
            result.setVicinity(jsonObject.getString("vicinity"));
            result.setPlace_id(jsonObject.getString("place_id"));

            if (!jsonObject.isNull("opening_hours")) {
                ArrayList<String> list_weekdays = new ArrayList<>();
                JSONArray openhoursArr = jsonObject.getJSONObject("opening_hours").getJSONArray("weekday_text");
                for (int i = 0; i < openhoursArr.length(); i++) {
                    try {
                        list_weekdays.add(openhoursArr.getString(i));
                    } catch (Exception e) {
                        Log.e("JSONObj, ", "kein Review Array");
                    }
                }

                result.setWeekdays(list_weekdays);
            } else {
                Log.e("JSONObj, ", "openninghour ist null");
            }

            // Extracting rating, if available
            if (!jsonObject.isNull("rating")) {
                result.setRating(jsonObject.getDouble("rating"));
            }
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Place.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<String> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(ArrayList<String> weekdays) {
        this.weekdays = weekdays;
    }

    public String getOpenhours() {
        return openhours;
    }

    public void setOpenhours(String openhours) {
        this.openhours = openhours;
    }

    public ArrayList<Review> getArrRev() {
        return arrRev;
    }

    public void setArrRev(ArrayList<Review> arrRev) {
        this.arrRev = arrRev;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Place{icon=" + icon + ", name=" + name + ",Adresse=" + vicinity;
    }


}
