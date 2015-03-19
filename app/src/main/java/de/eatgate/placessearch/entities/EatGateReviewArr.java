package de.eatgate.placessearch.entities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Shi on 30.03.2015.
 */
public class EatGateReviewArr {


    private ArrayList<EatGateReview> arrRev;

    /**
     * liefert das EatGateReview Objekt
     *
     * @param jsonArr
     * @return
     */
    public static EatGateReviewArr jsonToEatGateReviewArr(JSONArray jsonArr) {
        try {
            EatGateReviewArr result = new EatGateReviewArr();
            if ((jsonArr == null) || (jsonArr.length() == 0)) {
                Log.i(" JSONObj ", " kein EatGate Review Array oder Array leer! ");
                // toDo Fehlerbehandlung
            } else {
                ArrayList<EatGateReview> arrReviews = new ArrayList<EatGateReview>();
                for (int i = 0; i < jsonArr.length(); i++) {
                    arrReviews.add(EatGateReview.jsonToEatGateReview((JSONObject) jsonArr.get(i)));
                }
                result.setArrRev(arrReviews);
                return result;
            }
        } catch (JSONException e) {
            Log.e(" JSONObj ", " Fehler EatGate Review Array! ");
            // toDo Fehlerbehandlung
        }
        return null;
    }

    public ArrayList<EatGateReview> getArrRev() {
        return arrRev;
    }

    public void setArrRev(ArrayList<EatGateReview> arrRev) {
        this.arrRev = arrRev;
    }

    @Override
    public String toString() {
        String str = "EatGateReviews: [";
        for (int index = 0; index < arrRev.size(); index++) {
            str = str + arrRev.get(index).getInhalt() + "," + arrRev.get(index).getAuthorNickname()
                    + "," + arrRev.get(index).getVoting();
        }
        str = str + "]";
        return str;
    }
}
