package com.example.ohee.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquareImgView extends androidx.appcompat.widget.AppCompatImageView {

    public SquareImgView(Context context) {
        super(context);
    }

    public SquareImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}