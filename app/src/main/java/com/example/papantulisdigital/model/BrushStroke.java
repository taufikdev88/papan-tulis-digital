package com.example.papantulisdigital.model;

import java.io.Serializable;
import android.graphics.Path;
import java.util.LinkedList;

public class BrushStroke implements Serializable {
    private int mColor;
    private int mSizeOfBrush;
    private transient Path mPathOfStroke = null;
    private LinkedList<float[]> mPathPoints = new LinkedList<>();

    public BrushStroke(int selectedColor, int sizeOfBrush){
        this.mColor = selectedColor;
        this.mSizeOfBrush = sizeOfBrush;
        this.mPathPoints= new LinkedList<>();
        this.convertFromPointsToPath();
    }

    public int getColor() {
        return mColor;
    }

    public int getSizeOfBrush() {
        return mSizeOfBrush;
    }

    public void moveTo(float x, float y) {
        getPathOfStroke().moveTo(x, y);
        this.mPathPoints.add(new float[]{x, y});
    }

    public void lineTo(float x, float y) {
        getPathOfStroke().lineTo(x, y);
        this.mPathPoints.add(new float[]{x, y});
    }

    public Path getPathOfStroke() {
        this.convertFromPointsToPath();
        return mPathOfStroke;
    }

    private void convertFromPointsToPath() {
        if (this.mPathOfStroke != null){
            return;
        }

        this.mPathOfStroke = new Path();
        if (this.mPathPoints == null || this.mPathPoints.isEmpty()){
            return;
        }

        float[] initPoints = mPathPoints.getFirst();
        this.mPathOfStroke.moveTo(initPoints[0],initPoints[1]);
        for (float[] pointSet : mPathPoints){
            this.mPathOfStroke.lineTo(pointSet[0],pointSet[1]);
        }
    }
}
