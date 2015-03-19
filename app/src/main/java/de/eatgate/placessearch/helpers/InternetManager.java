package de.eatgate.placessearch.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by Shi on 22.03.2015.
 */
public class InternetManager {

    public static boolean checkInternetTelephon(Context mContext) {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.
                getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    // not implemented yet
    public static boolean checkInternetWLAN(Context mContext) {
        return true;
    }

    /**
     * Check Internet Connection
     *
     * @param mContext
     * @return
     */
    public static boolean isOnline(Context mContext) {
        final ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // notify user you are online
            return true;
        } else {
            // notify user you are not online
            // Toast.makeText(mContext.getApplicationContext(), "Internet ist offline!", Toast.LENGTH_LONG).show();
            return false;
        }
    }



     /*   telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
        Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
        getITelephonyMethod.setAccessible(true);
        ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
        ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

        if (isEnabled) {
            dataConnSwitchmethod = ITelephonyClass
                    .getDeclaredMethod("disableDataConnectivity");
        } else {
            dataConnSwitchmethod = ITelephonyClass
                    .getDeclaredMethod("enableDataConnectivity");
        }
        dataConnSwitchmethod.setAccessible(true);
        dataConnSwitchmethod.invoke(ITelephonyStub);*/


}
