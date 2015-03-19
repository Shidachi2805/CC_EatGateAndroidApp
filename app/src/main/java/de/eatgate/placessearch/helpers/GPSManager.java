package de.eatgate.placessearch.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import de.eatgate.placessearch.entities.GPS;

/**
 * Created by Shi on 22.03.2015.
 */
public class GPSManager implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private final Context mContext;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location = null; // location
    double latitude; // latitude
    double longitude; // longitude
    TextView textViewGPS = null;
    private AlertDialog dialog;
    private GPS gps;

    public GPSManager(Context context, TextView ch) {
        this.mContext = context;
        this.textViewGPS = ch;
        gps = new GPS(mContext);
    }

    public boolean checkCpsConNw() {
        Location loc = null;
        boolean bGPS = false;
        try {
            loc = gps.getLocation();
            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
            Log.i("GPS", "GPS Enabled");
            List<Address> addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            Log.i("GPS", addresses.get(0).getLocality());
            if (addresses.size() > 0) {
                textViewGPS.setText("Letzte bekannte Position: " + addresses.get(0).getLocality());
            } else {
                textViewGPS.setText(" Letzte bekannte Position: unbekannt !");
            }
        } catch (Exception ex) {
            Log.e("GPS", " Fehler in GPSManager! " + ex.getMessage());
            textViewGPS.setText(" Letzte bekannte Position: unbekannt !");
        }
        return bGPS;
    }

    public boolean isNetwork() {
        return gps.isNetworkEnabled();
    }

    public boolean isGPS() {
        return gps.isGPSEnabled();
    }

    public boolean checkGpsConDialog() {
        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        return false;
    }

 /*   public boolean checkGpsCon()
    {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(mContext.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(!isGPSEnabled) {
                buildAlertMessageNoGps();
                textViewGPS.setText(" Letzte bekannte Position: unbekannt !");
            } else {
                Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
                // location  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("GPS", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Log.i("GPS", addresses.get(0).getLocality());
                    if (addresses.size() > 0) {
                        textViewGPS.setText("Letzte bekannte Position: " + addresses.get(0).getLocality());
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            Log.e("GPS", " Fehler in GPSManager!!!" + ex.getMessage());
            // checkBoxGPS.setChecked(false);
            textViewGPS.setText(" Letzte bekannte Position: unbekannt !");
            return false;
        }
    } */


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(" GPS offline, einschalten ?")
                .setCancelable(false)
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                        // checkBoxGPS.setChecked(true);
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // nichts weiter tun; Dialog schließen
                        dialog.dismiss();
                        // checkBoxGPS.setChecked(false);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private interface ICommand {
        void execute();
    }


    // not used
    private class CancelCommand implements ICommand {
        protected Activity m_activity;

        public CancelCommand(Activity activity) {
            m_activity = activity;
        }

        public void execute() {
            dialog.dismiss();
            //start asyncronous operation here
        }
    }


    private class EnableGpsCommand extends CancelCommand {
        public EnableGpsCommand(Activity activity) {
            super(activity);
        }

        public void execute() {
            // take the user to the phone gps settings and then start the asyncronous logic.
            m_activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            super.execute();
        }
    }
}
