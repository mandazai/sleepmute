package sample.datetimepicker.fourmob.com.datetimesample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.sleepbot.datetimepicker.time.RadialTimeView;

public class MainActivity extends FragmentActivity {
//    public static final String PREF_AIRPLANE_ON_OFF = "airplane_mode";

    private RadialTimeView mTimeView;
    private int mStartHour;
    private int mStartMinute;
    private int mEndHour;
    private int mEndMinute;

    private boolean mAirplaneMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.radial_time_circle);

        mTimeView = (RadialTimeView) findViewById(R.id.time_circle);
        getTimePreferences();

        if (mAirplaneMode) {
            mTimeView.setTimeText(mStartHour, mStartMinute, mEndHour, mEndMinute);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.switch_airplane);
        View view = MenuItemCompat.getActionView(menuItem);
        Switch switchButton = (Switch) view.findViewById(R.id.switch_button);
        if (mAirplaneMode) {
            switchButton.setChecked(true);
            enableDisableViewGroup(mTimeView, false);
        }
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do anything here on check changed
                if (isChecked) {
                    enableDisableViewGroup(mTimeView, false);
                    setTimeAirModeOn();
                    PollService.setAirplaneTime(mStartHour, mStartMinute, mEndHour, mEndMinute);
                    PollService.setServiceAlarm(MainActivity.this, true);
                    saveTimePreferences(mStartHour, mStartMinute, mEndHour, mEndMinute);
                } else {
                    enableDisableViewGroup(mTimeView, true);
                    PollService.setServiceAlarm(MainActivity.this, false);
                    saveAirplaneMode(false);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void saveTimePreferences(int startHour, int startMinute, int endHour, int endMinute) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt(PollService.PREF_START_HOUR, startHour)
                    .putInt(PollService.PREF_START_MINUTE, startMinute)
                    .putInt(PollService.PREF_END_HOUR, endHour)
                    .putInt(PollService.PREF_END_MINUTE, endMinute)
                    .putBoolean(PollService.PREF_IS_ALARM_ON, true)
                    .commit();
    }

    private void setTimeAirModeOn() {
        mStartHour = mTimeView.getTimeText(0, true);
        mStartMinute = mTimeView.getTimeText(0, false);
        mEndHour = mTimeView.getTimeText(1, true);
        mEndMinute = mTimeView.getTimeText(1, false);
        mAirplaneMode = true;
    }

    private void saveAirplaneMode(boolean isAirplaneOn) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean(PollService.PREF_IS_ALARM_ON, isAirplaneOn)
                .commit();
    }

    private void getTimePreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mStartHour = prefs.getInt(PollService.PREF_START_HOUR, 0);
        mStartMinute = prefs.getInt(PollService.PREF_START_MINUTE, 0);
        mEndHour = prefs.getInt(PollService.PREF_END_HOUR, 0);
        mEndMinute = prefs.getInt(PollService.PREF_END_MINUTE, 0);
        mAirplaneMode = prefs.getBoolean(PollService.PREF_IS_ALARM_ON, false);
    }

    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
}
