package de.eatgate.placessearch.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Shi on 30.03.2015.
 */
public class EatGateReview {


    private String nickname;
    private String authorUrl;
    private String voting;
    private String inhalt;
    private String time;

    private EatGateReview() {

    }

    public static EatGateReview jsonToEatGateReview(JSONObject jsonObject) {
        try {
            EatGateReview result = new EatGateReview();
            result.setVoting(jsonObject.getString("Voting"));
            result.setAuthorNickname(jsonObject.getString("Nickname"));
            result.setInhalt(jsonObject.getString("Inhalt"));
            // toDo noch die restlichen Datenfelder
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(EatGateReview.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getAuthorNickname() {
        return nickname;
    }

    public void setAuthorNickname(String authorName) {
        this.nickname = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getVoting() {
        return voting;
    }

    public void setVoting(String rating) {
        this.voting = rating;
    }

    public String getInhalt() {
        return inhalt;
    }

    public void setInhalt(String text) {
        this.inhalt = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{ " + getInhalt() + "," + getAuthorNickname() + getVoting() + " }";
    }
}
