package com.ckt.eirot.changecolor;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class MainActivity extends AppCompatActivity {
    private SimpleDraweeView mUserSimpleDraweeView;
    /**
     * {@link ColorDrawable} used to draw the window's background.
     */
    private ColorDrawable mBackground;

    /**
     * Duration in millis to animate changes to the background color.
     */
    private static final long BACKGROUND_COLOR_ANIMATION_DURATION = 3000L;

    /**
     * {@link BroadcastReceiver} to update the background color whenever the system time changes.
     */
    private BroadcastReceiver mOnTimeChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mUserSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.user);
        Uri uri = Uri.parse("http://img3.duitang.com/uploads/item/201606/19/20160619102001_QMGTU.jpeg");
        mUserSimpleDraweeView.setImageURI(uri);
        mUserSimpleDraweeView.setOnClickListener(tapListener);
        mUserSimpleDraweeView.setBackgroundColor(Utils.getCurrentHourColor());
        setChangeBackgroundColor(Utils.getCurrentHourColor(), false /* animate */);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register mOnTimeChangedReceiver to update current background color periodically.
        if (mOnTimeChangedReceiver == null) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(mOnTimeChangedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    /*
                     * Note that window background color hava animation, mUserSimpleDraweeView not.
                     * So color change can't be synchronous, set animate to false for change color synchronous
                     */
                    mUserSimpleDraweeView.setBackgroundColor(Utils.getCurrentHourColor());
                    setChangeBackgroundColor(Utils.getCurrentHourColor(), true /* animate */);
                }
            }, filter);
        }

        // Ensure the background color is up-to-date.
        mUserSimpleDraweeView.setBackgroundColor(Utils.getCurrentHourColor());
        setChangeBackgroundColor(Utils.getCurrentHourColor(), true /* animate */);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop updating the background color when not active.
        if (mOnTimeChangedReceiver != null) {
            unregisterReceiver(mOnTimeChangedReceiver);
            mOnTimeChangedReceiver = null;
        }
    }

    private View.OnClickListener tapListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "You tap me !", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * Sets the current background color to the provided value and animates the change if desired.
     *
     * @param color the ARGB value to set as the current background color
     * @param animate {@code true} if the change should be animated
     */
    protected void setChangeBackgroundColor(int color, boolean animate) {
        if (mBackground == null) {
            mBackground = new ColorDrawable(color);
            getWindow().setBackgroundDrawable(mBackground);
        }

        if (mBackground.getColor() != color) {
            if (animate) {
                ObjectAnimator.ofObject(mBackground, "color", new ArgbEvaluator(), color)
                        .setDuration(BACKGROUND_COLOR_ANIMATION_DURATION)
                        .start();
            } else {
                mBackground.setColor(color);
            }
        }
    }
}
