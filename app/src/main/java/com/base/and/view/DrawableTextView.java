package com.base.and.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * drawableLeft等与文本一起居中显示
 */
public class DrawableTextView extends AppCompatTextView {
    public DrawableTextView(Context context) {
        super(context);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableTextView(Context context, AttributeSet attrs,
                            int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            if (drawableLeft != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableLeft.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
            Drawable drawableRight = drawables[2];
            if (drawableRight != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableRight.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                setPadding(0, 0, (int) (getWidth() - bodyWidth), 0);
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}