package es.upsa.mimo.musicfest.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Javier on 15/05/2017.
 */

public class OnBootReceiver extends BroadcastReceiver {
    private static final String ALARM_ACTION = "es.upsa.mimo.musicfest.services.ALARM_ACTION";

    public void onReceive(Context context, Intent intent) {
        Log.e("=== OnBootReceiver ===", "onReceive");

        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notications", true))
            return;

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("=== OnBootReceiver ===", "android.intent.action.BOOT_COMPLETED");
            //long interval = 1800000;
           /* try {
                interval = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString("sync_interval", "1800000"));
            }catch (NumberFormatException e) {
                Log.e("=== OnBootReceiver ===", "NumberFormatException");
            }*/

            context.startService(new Intent(context,AlarmManagerNotifications.class));

           /*FUNCIONA
            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent mIntent = new Intent(context, OnBootReceiver.class);
            mIntent.setAction(ALARM_ACTION);

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, 10); // For 10 AM
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
*/
        }
        else if (intent.getAction().equals(ALARM_ACTION)) {
            Log.e("=== OnBootReceiver ===", ALARM_ACTION);
            context.startService(new Intent(context,SoonEventsNotifications.class));
        }
    }


}
