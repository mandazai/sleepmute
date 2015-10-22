package sample.datetimepicker.fourmob.com.datetimesample;
import java.util.Calendar;
import android.app.AlarmManager;

/**
 * Created by tstcit on 2015/10/20.
 */
public class Utils {
    public static Interval getInterval(int startHour, int startMinute, int endHour, int endMinute) {
        Interval intervals = new Interval();
        long firstSecond, endSecond;
        Calendar currentTime = Calendar.getInstance();
        Calendar firstTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();

        setTime(startHour, startMinute, firstTime);
        setTime(endHour, endMinute, endTime);

        firstSecond = firstTime.getTimeInMillis() - currentTime.getTimeInMillis();
        if (firstSecond <= 0) {
            firstSecond += AlarmManager.INTERVAL_DAY;
        }

        endSecond = endTime.getTimeInMillis() - currentTime.getTimeInMillis();
        if (endSecond <= 0) {
            endSecond += AlarmManager.INTERVAL_DAY;
        }

        if (firstSecond < endSecond) {
            intervals.isDay = true;
            intervals.first = firstSecond;
        } else {
            intervals.isDay = false;
            intervals.first = endSecond;
        }

        intervals.night = endTime.getTimeInMillis() - firstTime.getTimeInMillis();
        if (intervals.night <= 0) {
            intervals.night += AlarmManager.INTERVAL_DAY;
        }
        intervals.day = AlarmManager.INTERVAL_DAY - intervals.night;
        return intervals;
    }

    public static void setTime(int hour, int minute, Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    static class Interval {
        public long first;
        public long day;
        public long night;
        public boolean isDay;
    }
}
