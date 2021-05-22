package com.example.papantulisdigital.view;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

public class ColorSeekView extends AppCompatSeekBar {

    public ColorSeekView(Context context) {
        this(context, null);
    }

    public ColorSeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float x1 = getLeft();
        float x2 = getRight();
        float padX2 = getPaddingRight();
        float padX1 = getPaddingLeft();

        LinearGradient overlay = new LinearGradient(x1, 0.0f, x2-padX1-padX2, 0.0f,
                new int[]{0xFF000000, 0xFF0000FF, 0xFF00FF00, 0xFF00FFFF,
                        0xFFFF0000, 0xFFFF00FF, 0xFFFFFF00, 0xFFFFFFFF},
                null, Shader.TileMode.CLAMP);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(overlay);
        setProgressDrawable(shape);
    }
}
