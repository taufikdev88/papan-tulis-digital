package com.example.papantulisdigital.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.papantulisdigital.model.BrushStroke;
import com.example.papantulisdigital.model.SavedArt;
import com.example.papantulisdigital.model.TouchScreenArt;
import com.example.papantulisdigital.view.CanvasAreaView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import static android.graphics.Color.BLACK;

public class PaintActivity extends AppCompatActivity implements CanvasAreaView.CanvasController, TouchScreenArt.SavedArtReadListener, SizePickerFragment.SizePickerListener, ColorPickerFragment.ColorPickerListener {
    private static final int DEFAULT_COLOR = BLACK;
    private static final int DEFAULT_BRUSH_SIZE = 25;
    private static final String PAINT_NAME = "paintName";
    public static final String PREFERENCES = "paint_pref";
    public static final String COLOR = "color";
    public static final String PROGRESS = "progress";
    public static final String ART_BUNDLE_ID = "art";

    private TouchScreenArt mArt;
    private int mSelectedColor;
    private int mBrushSize = DEFAULT_BRUSH_SIZE;
    private final Paint mPaintConfig = new Paint();
    private String mSavedName;

    private FloatingActionButton mBrushColorButton;
    private FloatingActionButton mBrushSizeButton;
    private FloatingActionButton fabClear;
    private FloatingActionButton fabUndo;
    private FloatingActionButton fabSave;
    private CanvasAreaView mCanvasView;

    final Timer timer = new Timer();

    @Override
    public void fingerTouchedAt(float x, float y) {
        mBrushColorButton.setVisibility(View.GONE);
        mBrushSizeButton.setVisibility(View.GONE);
        fabClear.setVisibility(View.GONE);
        fabUndo.setVisibility(View.GONE);
        fabSave.setVisibility(View.GONE);

        BrushStroke stroke = new BrushStroke(mSelectedColor,mBrushSize);
        stroke.moveTo(x,y);
        mArt.modifyPicture(stroke);
    }

    @Override
    public void fingerMovedTo(float x, float y) {
        BrushStroke stroke = mArt.getCurrentChange();
        stroke.lineTo(x,y);
    }

    @Override
    public void fingerRaised(float x, float y) {
        mBrushSizeButton.setVisibility(View.VISIBLE);
        mBrushColorButton.setVisibility(View.VISIBLE);
        fabSave.setVisibility(View.VISIBLE);
        fabUndo.setVisibility(View.VISIBLE);
        fabClear.setVisibility(View.VISIBLE);

        try {
            Bitmap bitmap = Bitmap.createBitmap(mCanvasView.getWidth(), mCanvasView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            mCanvasView.draw(canvas);

            String base64str = encodeBase64(bitmap);
//            Bitmap resized = resizeBitmap(bitmap);
            Log.d("DEBUG_", "Length: " + base64str.length());
            if(base64str.length() > 165000){
                Toast.makeText(this, "Gambar sudah terlalu besar!!!", Toast.LENGTH_LONG).show();
            } else {
                new Thread(new SplashActivity.SendThread(base64str)).start();
            }
        } catch (Exception e) {
            Log.d("DEBUG_", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void drawPicture(Canvas canvas) {
        for (BrushStroke stroke : mArt.allStrokes()){
            mPaintConfig.setColor(stroke.getColor());
            mPaintConfig.setStrokeWidth(stroke.getSizeOfBrush());
            canvas.drawPath(stroke.getPathOfStroke(), mPaintConfig);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        if(intent.getExtras() != null){
            mSavedName = intent.getExtras().getString(PAINT_NAME);
        }

        if(savedInstanceState != null){
            mArt = (TouchScreenArt) savedInstanceState.getSerializable(ART_BUNDLE_ID);
        } else if(mSavedName != null && !mSavedName.isEmpty()){
            mArt = new TouchScreenArt(this);
            mArt.retrieve(mSavedName, this);
        } else {
            mArt = new TouchScreenArt(this);
            mSavedName = "";
        }

        mCanvasView = findViewById(R.id.paintCanvasView);
        setupBrushColor();
        setupBrushSize();
        mPaintConfig.setStyle(Paint.Style.STROKE);

        fabUndo = findViewById(R.id.paintFabUndo);
        fabUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoChange();
            }
        });
        fabClear = findViewById(R.id.paintFabClear);
        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPicture();
            }
        });
        fabSave = findViewById(R.id.paintFabSave);
        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleExit();
            }
        });

//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    Bitmap bitmap = Bitmap.createBitmap(mCanvasView.getWidth(), mCanvasView.getHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    canvas.drawColor(Color.WHITE);
//                    mCanvasView.draw(canvas);
//
//                    Bitmap resized = resizeBitmap(bitmap);
//                    new Thread(new SplashActivity.SendThread(encodeBase64(resized))).start();
//                } catch (Exception e) {
//                    Log.d("DEBUG_", e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }, 0, 500);
    }

    public static String encodeBase64(Bitmap image){
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte b[] = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) {
            Log.d("DEBUG_", e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public static Bitmap resizeBitmap(Bitmap bitmap){
        try {
            final int fixed_height = 720;
            float height_percent = (fixed_height / (float) bitmap.getHeight());
            int width_size = (int) ((float) bitmap.getWidth() * height_percent);

            Matrix matrix = new Matrix();
            matrix.postScale(width_size, fixed_height);
            Bitmap resized = Bitmap.createBitmap(bitmap, 0, 0, width_size, fixed_height, matrix, false);
            return resized;
        } catch (Exception ex){
            Log.d("DEBUG_", ex.getMessage());
            ex.printStackTrace();
            return Bitmap.createBitmap(bitmap);
        }
    }

    private void clearPicture() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus")
                .setMessage("Apakah anda yakin untuk menghapus semua layar ?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mArt.clear();
                        CanvasAreaView canvasAreaView = findViewById(R.id.paintCanvasView);
                        canvasAreaView.invalidate();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void undoChange() {
        mArt.undoLastChange();
        mCanvasView.invalidate();
    }

    private void setupBrushSize() {
        mBrushSizeButton = findViewById(R.id.paintFabBrush);
        mBrushSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager manager = getFragmentManager();
                android.app.Fragment frag = manager.findFragmentByTag("fragment_brush_size_selection");
                if(frag != null){
                    manager.beginTransaction().remove(frag).commit();
                }
                SizePickerFragment alertDialogFragment = new SizePickerFragment();
                alertDialogFragment.show(manager, "fragment_brush_size_selection");
            }
        });
    }

    @Override
    public void onBackPressed() {
        handleExit();
    }

    private void handleExit() {
        final EditText input = new EditText(this);
        input.setText(mSavedName);
        input.requestFocus();
        input.setPadding(100,50,100,50);

        DialogInterface.OnClickListener saveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if(mSavedName.equals(text)){
                    mArt.save(text, getApplicationContext());
                    finish();
                } else if(SavedArt.getInstance(getApplicationContext()).addArt(text)){
                    mArt.save(text, getApplicationContext());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("saved",true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    input.setHint("Nama sudah ada");
                    input.clearComposingText();
                }
            }
        };

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Simpan")
                .setMessage("Mohon isikan nama untuk papan ini")
                .setView(input)
                .setPositiveButton("Simpan", saveListener)
                .setNegativeButton("Buang", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();

        if(dialog != null){
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            dialog.show();
        }
    }

    private void setupBrushColor() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(PaintActivity.PREFERENCES, Context.MODE_PRIVATE);
        mSelectedColor = sharedPreferences.getInt(COLOR, DEFAULT_COLOR);
        mBrushColorButton = findViewById(R.id.paintFabColor);
        mBrushColorButton.setBackgroundTintList(ColorStateList.valueOf(mSelectedColor));
        mBrushColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager manager = getFragmentManager();
                android.app.Fragment frag = manager.findFragmentByTag("fragment_brush_color_selection");
                if(frag != null){
                    manager.beginTransaction().remove(frag).commit();
                }
                ColorPickerFragment alertDialogFragment = new ColorPickerFragment();
                alertDialogFragment.show(manager, "fragment_brush_color_selection");
            }
        });
    }

    @Override
    public void notifyLoaded(Stack<BrushStroke> strokes) {
        mCanvasView.invalidate();
    }

    @Override
    public void onConfirmColorPick(int color) {
        mSelectedColor = color;
        mBrushColorButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void onConfirmSizePick(int size) {
        mBrushSize = size;
    }

    @Override
    public int getCurrentSize() {
        return 0;
    }
}