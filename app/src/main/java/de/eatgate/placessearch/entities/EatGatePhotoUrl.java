package de.eatgate.placessearch.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Shi on 31.03.2015.
 */
public class EatGatePhotoUrl {

    private String photoUrl;
    private String id;
    private String locationId;

    private EatGatePhotoUrl() {

    }

    /**
     * Konvertiert das jsonObjekt vom Server in ein EatGatePhotoUrl Objekt
     *
     * @param jsonObject
     * @return
     */
    public static EatGatePhotoUrl jsonToEatGatePhotoUrl(JSONObject jsonObject) {
        try {
            EatGatePhotoUrl result = new EatGatePhotoUrl();
            result.setPhotoUrl(jsonObject.getString("Filepath"));
            // toDo noch die restlichen Datenfelder
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(EatGatePhotoUrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "{ " + getPhotoUrl() + " }";
    }
}
