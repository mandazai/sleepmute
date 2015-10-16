package com.sleepbot.datetimepicker.time;

/**
 * Created by tstcit on 2015/10/15.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fourmob.datetimepicker.R;
import com.fourmob.datetimepicker.Utils;

import java.util.Locale;

/**
 * Simple UserView that wraps multiple Views together. Does nothing else.
 */
public class RadialTimeView extends LinearLayout implements RadialPickerLayout.OnValueSelectedListener {

    private static final int HOUR_INDEX = TimePickerDialog.HOUR_INDEX;
    private static final int MINUTE_INDEX = TimePickerDialog.MINUTE_INDEX;

    // Delay before starting the pulse animation, in ms.
    private static final int PULSE_ANIMATOR_DELAY = 300;

    private TextView mHourView;
    private TextView mHourSpaceView;
    private TextView mMinuteView;
    private TextView mMinuteSpaceView;
    private TextView mAmPmTextView;

    private int mBlue;
    private int mBlack;

    private int mHour;
    private int mMinute;

    private boolean mAllowAutoAdvance = true;

    private RadialPickerLayout mTimePicker;
    public RadialTimeView(Context context) {
        this(context, null);
    }

    public RadialTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Clock,
                0, 0
        );

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.PieChart_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.

            mHour = a.getInt(R.styleable.Clock_hour, 0);
            mMinute = a.getInt(R.styleable.Clock_minute, 0);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }
        mBlue = getResources().getColor(R.color.blue);
        mBlack = getResources().getColor(R.color.numbers_text_color);

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.time_view_merge, this);

        mHourView = (TextView) findViewById(R.id.hours);
        mHourSpaceView = (TextView) findViewById(R.id.hour_space);
        mMinuteSpaceView = (TextView) findViewById(R.id.minutes_space);
        mMinuteView = (TextView) findViewById(R.id.minutes);
        mAmPmTextView = (TextView) findViewById(R.id.ampm_label);
        mAmPmTextView.setVisibility(View.GONE);

        setHour(mHour, false);
        setMinute(mMinute);

        mTimePicker = (RadialPickerLayout) findViewById(R.id.time_view);
        mTimePicker.setOnValueSelectedListener(this);
        mTimePicker.initialize(context, mHour, mMinute, true, false);

        setCurrentItemShowing(HOUR_INDEX, false, true, true);
        mTimePicker.invalidate();

        mHourView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(HOUR_INDEX, true, false, true);
            }
        });
        mMinuteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentItemShowing(MINUTE_INDEX, true, false, true);
            }
        });
    }
    // Show either Hours or Minutes.
    private void setCurrentItemShowing(int index, boolean animateCircle, boolean delayLabelAnimate,
                                       boolean announce) {
        mTimePicker.setCurrentItemShowing(index, animateCircle);

        TextView labelToAnimate;
        if (index == HOUR_INDEX) {
            int hours = mTimePicker.getHours();
//            if (!mIs24HourMode) {
//                hours = hours % 12;
//            }
//            mTimePicker.setContentDescription(mHourPickerDescription + ": " + hours);
//            if (announce) {
//                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectHours);
//            }
            labelToAnimate = mHourView;
        } else {
            int minutes = mTimePicker.getMinutes();
//            mTimePicker.setContentDescription(mMinutePickerDescription + ": " + minutes);
//            if (announce) {
//                Utils.tryAccessibilityAnnounce(mTimePicker, mSelectMinutes);
//            }
            labelToAnimate = mMinuteView;
        }

        int hourColor = (index == HOUR_INDEX) ? mBlue : mBlack;
        int minuteColor = (index == MINUTE_INDEX) ? mBlue : mBlack;
        mHourView.setTextColor(hourColor);
        mMinuteView.setTextColor(minuteColor);

        com.nineoldandroids.animation.ObjectAnimator pulseAnimator = Utils.getPulseAnimator(labelToAnimate, 0.85f, 1.1f);
        if (delayLabelAnimate) {
            pulseAnimator.setStartDelay(PULSE_ANIMATOR_DELAY);
        }
        pulseAnimator.start();
    }


    private void setHour(int value, boolean announce) {
        String format;
        format = "%02d";

        CharSequence text = String.format(format, value);
        mHourView.setText(text);
        mHourSpaceView.setText(text);
        if (announce) {
            Utils.tryAccessibilityAnnounce(mTimePicker, text);
        }
    }

    private void setMinute(int value) {
        if (value == 60) {
            value = 0;
        }
        CharSequence text = String.format(Locale.getDefault(), "%02d", value);
        Utils.tryAccessibilityAnnounce(mTimePicker, text);
        mMinuteView.setText(text);
        mMinuteSpaceView.setText(text);
    }

    /**
     * Called by the picker for updating the header display.
     */
    @Override
    public void onValueSelected(int pickerIndex, int newValue, boolean autoAdvance) {
        if (pickerIndex == HOUR_INDEX) {
            setHour(newValue, false);
            String announcement = String.format("%d", newValue);
            if (mAllowAutoAdvance && autoAdvance) {
                setCurrentItemShowing(MINUTE_INDEX, true, true, false);
//                announcement += ". " + mSelectMinutes;
            }
//            Utils.tryAccessibilityAnnounce(mTimePicker, announcement);
        } else if (pickerIndex == MINUTE_INDEX) {
            setMinute(newValue);
        }
    }
}