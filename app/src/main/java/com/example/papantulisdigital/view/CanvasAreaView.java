package com.example.papantulisdigital.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class CanvasAreaView extends View {
    public interface CanvasController {
        void fingerTouchedAt(float x, float y);
        void fingerMovedTo(float x, float y);
        void fingerRaised(float x, float y);
        void drawPicture(Canvas canvas);
    }

    private CanvasController canvasController;
    public CanvasAreaView(Context context){
        this(context, null);
    }
    public CanvasAreaView(Context context, @Nullable AttributeSet attrs){
        super(context, attrs);

        if(!(context instanceof  CanvasController)){
            return;
        }
        canvasController = (CanvasController) context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float xPos = event.getX();
        final float yPos = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                canvasController.fingerTouchedAt(xPos, yPos);
                break;
            case MotionEvent.ACTION_MOVE:
                canvasController.fingerMovedTo(xPos, yPos);
                break;
            case MotionEvent.ACTION_UP:
                canvasController.fingerRaised(xPos, yPos);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvasController.drawPicture(canvas);
    }
}
