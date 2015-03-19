package de.eatgate.placessearch.global;

import android.app.Application;

import de.eatgate.placessearch.entities.PlaceDetails;

/**
 * Created by Shi on 10.03.2015.
 */
public class AppGob extends Application {
    public static PlaceDetails g_placeDetails;
    public static String mCurrentPhotoPath;
    public static int mUserId;
}
