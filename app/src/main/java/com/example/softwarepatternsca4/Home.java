package com.example.softwarepatternsca4;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Objects;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Online catalogue");
    }
}