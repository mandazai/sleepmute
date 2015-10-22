package sample.datetimepicker.fourmob.com.datetimesample;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

public class PollService extends IntentService {
    private static final String TAG = "PollService";

    public static final String PREF_IS_ALARM_ON = "isAlarmOn";

    public static final String PREF_START_HOUR = "start_hour";
    public static final String PREF_START_MINUTE = "start_minute";

    public static final String PREF_END_HOUR = "end_hour";
    public static final String PREF_END_MINUTE = "end_minute";

    private static final int AIRPLANE_ON_MSG = 1;
    private static final int AIRPLANE_OFF_MSG = 2;

    private static long firstInterval;
    private static long nightInterval;
    private static long dayInterval;

    private static int mStartHour;
    private static int mStartMinute;
    private static int mEndHour;
    private static int mEndMinute;

    private static boolean isDay;
    
    public PollService() {
        super(TAG);
    }

    @Override
    public void onHandleIntent(Intent intent) {
        if (firstInterval == 0) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mStartHour = prefs.getInt(PollService.PREF_START_HOUR, 0);
            mStartMinute = prefs.getInt(PollService.PREF_START_MINUTE, 0);
            mEndHour = prefs.getInt(PollService.PREF_END_HOUR, 0);
            mEndMinute = prefs.getInt(PollService.PREF_END_MINUTE, 0);
            setAirplaneTime(mStartHour, mStartMinute, mEndHour, mEndMinute);
            setServiceAlarm(getApplicationContext(), true);
            return;
        }

        Log.d(TAG, "auto airplane run");

        AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (isDay) {
            firstInterval = nightInterval;

            //mute audio
            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            Log.d(TAG, "airplane on");
        } else {
            firstInterval = dayInterval;
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.d(TAG, "airplane off");
        }
        setServiceAlarm(getApplicationContext(), true);
        isDay = !isDay;
    }

    public static void setAirplaneTime(int startHour, int startMinute, int endHour, int endMinute) {
        Utils.Interval intervals = Utils.getInterval(startHour, startMinute, endHour, endMinute);
        firstInterval = intervals.first;
        nightInterval = intervals.night;
        dayInterval = intervals.day;
        isDay = intervals.isDay;
        Log.d(TAG, "firstInterval: " + firstInterval + " nightInterval: " + nightInterval + " dayInterval: " + dayInterval + " isDay: " + isDay);
    }
    
    public static void setServiceAlarm(Context context, boolean isOn) {
        Log.d(TAG, "setServiceAlarm " + isOn + " " + firstInterval);
        Intent i = new Intent(context, PollService.class);
        PendingIntent pi = PendingIntent.getService(
                context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, firstInterval + System.currentTimeMillis(), pi);
            } else {
                alarmManager.set(AlarmManager.RTC, firstInterval + System.currentTimeMillis(), pi);
            }

        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
