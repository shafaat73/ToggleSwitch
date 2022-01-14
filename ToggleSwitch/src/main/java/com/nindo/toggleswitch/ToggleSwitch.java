package com.nindo.toggleswitch;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BaseInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class ToggleSwitch extends RelativeLayout {


    private LayoutInflater mInflater;
    private ImageView switchThumb;
    private Context context;
    private State switchState = State.OFF;
    private Float thumbMargin = 0f;
    private int thumbOnColor;
    private int thumbOffColor;

    public interface OnViewClick {
        void onClickListener(State state);

    }

    enum State {
        ON, OFF
    }


    private OnViewClick mListener;

    public void setSwitchClickListener(
            OnViewClick onViewClick
    ) {
        mListener = onViewClick;
    }

    public ToggleSwitch(Context context) {
        super(context);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init(null);
    }

    public ToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init(attrs);
    }

    public ToggleSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init(attrs);
    }

    public void init(AttributeSet attrs) {

        View view = mInflater.inflate(R.layout.item_switch_layout, this, true);
        switchThumb = view.findViewById(R.id.switchThumb);
        RelativeLayout switchLayout = view.findViewById(R.id.switchLayout);


        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    setState();
                    mListener.onClickListener(switchState);
                }
            }
        });


        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomSwitch);

            if (a.hasValue(R.styleable.CustomSwitch_thumbSize)) {
                int margin = a.getDimensionPixelSize(R.styleable.CustomSwitch_thumbMargin, 0);
                thumbMargin = convertDpToPixel(margin, context);
                switchLayout.setPadding(margin, margin, margin, margin);

                int thumbSize = a.getDimensionPixelSize(R.styleable.CustomSwitch_thumbSize, 200);
                switchThumb.getLayoutParams().height = thumbSize;
                switchThumb.getLayoutParams().width = thumbSize;
                switchThumb.requestFocus();

                switchLayout.getLayoutParams().width = a.getDimensionPixelSize(R.styleable.CustomSwitch_trackWidth, 100);
                switchLayout.getLayoutParams().height = a.getDimensionPixelSize(R.styleable.CustomSwitch_trackHeight, 208);

                if (a.hasValue(R.styleable.CustomSwitch_trackBackground)) {
                    int trackBackground = a.getResourceId(R.styleable.CustomSwitch_trackBackground, 0);
                    Drawable drawable = getResources().getDrawable(trackBackground);
                    switchLayout.setBackground(drawable);

                    if (a.hasValue(R.styleable.CustomSwitch_trackBackgroundTint)) {
                        int trackBackgroundTint = a.getResourceId(R.styleable.CustomSwitch_trackBackgroundTint, 0);
                        DrawableCompat.setTint(
                                DrawableCompat.wrap(switchLayout.getBackground()),
                                ContextCompat.getColor(context, trackBackgroundTint)
                        );

                    }

                }
                thumbOnColor = a.getResourceId(R.styleable.CustomSwitch_thumbOnColor, R.color.switch_on);
                thumbOffColor = a.getResourceId(R.styleable.CustomSwitch_thumbOffColor, R.color.switch_off);
                if (switchState == State.OFF)
                    switchThumb.setColorFilter(ContextCompat.getColor(context, thumbOffColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    private void setState() {
        if (switchState == State.ON) {
            animateSwitchBounce(0f, switchThumb);
            switchThumb.setColorFilter(ContextCompat.getColor(context, thumbOffColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            switchState = State.OFF;
        } else {
            animateSwitchLinear((float) -(this.getHeight() - switchThumb.getHeight() - thumbMargin), switchThumb);
            switchThumb.setColorFilter(ContextCompat.getColor(context, thumbOnColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            switchState = State.ON;
        }

    }

    private void animateSwitchLinear(Float value, ImageView view) {
        view.animate()
                .translationY(value)
                .setInterpolator(new LinearInterpolator())
                .start();
    }

    private void animateSwitchBounce(Float value, ImageView view) {
        view.animate()
                .translationY(value)
                .setInterpolator(new BounceInterpolator())
                .start();
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
