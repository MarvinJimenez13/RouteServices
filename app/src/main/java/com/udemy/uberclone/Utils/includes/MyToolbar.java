package com.udemy.uberclone.Utils.includes;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class MyToolbar {

    public static void show(AppCompatActivity activity, String title, boolean upButton, MaterialToolbar toolbar){
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
    }

}
