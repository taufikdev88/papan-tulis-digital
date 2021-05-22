package com.example.papantulisdigital.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.papantulisdigital.model.SavedArt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
    implements AbsListView.MultiChoiceModeListener {
    private final static int ACTIVITY_ID = 100;

    private ListView mListView;
    private ArrayAdapter<String> mListAdapter;
    private ArrayList<String> mSelectedForDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        FloatingActionButton addArt = findViewById(R.id.mainAddArt);
        addArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paint = new Intent(MainActivity.this, PaintActivity.class);
                startActivityForResult(paint, ACTIVITY_ID);
            }
        });

        if(savedInstanceState != null){
            mSelectedForDelete = savedInstanceState.getStringArrayList("selectedForDelete");
        } else {
            mSelectedForDelete = new ArrayList<>();
        }

        LinkedList<String> artNames = SavedArt.getInstance(this).getArtNames();
        refreshHelperText(artNames);

        mListView = findViewById(R.id.mainList);
        mListAdapter  = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, artNames);
        mListView.setAdapter(mListAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String paintName = (String) mListView.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, PaintActivity.class);
                intent.putExtra("paintName",paintName);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ACTIVITY_ID:
                if(resultCode == Activity.RESULT_OK){
                    boolean added = data.getBooleanExtra("saved", false);
                    if(added){
                        LinkedList<String> artNames = SavedArt.getInstance(this).getArtNames();
                        refreshHelperText(artNames);
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SavedArt.getInstance(this).persistArtNames(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("selectedForDelete", mSelectedForDelete);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.list_action_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionDelete:
                for(String itemSelected : mSelectedForDelete){
                    SavedArt.getInstance(this).removeArt(itemSelected);
                }

                LinkedList<String> artNames = SavedArt.getInstance(this).getArtNames();
                refreshHelperText(artNames);

                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mSelectedForDelete.clear();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if(checked){
            mSelectedForDelete.add(mListAdapter.getItem(position));
        } else {
            mSelectedForDelete.remove(mListAdapter.getItem(position));
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void refreshHelperText(LinkedList<String> artNames) {
        TextView textView = findViewById(R.id.mainText);
        textView.setVisibility(artNames == null || artNames.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }
}