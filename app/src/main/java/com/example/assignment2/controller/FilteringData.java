package com.example.assignment2.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.assignment2.R;

import java.util.Objects;

public class FilteringData extends AppCompatActivity {
    protected RadioGroup radioGroup2;
    protected Button searchBtn;
    protected RadioButton dataSiteOwner;
    protected RadioButton dataSiteParticipants;

    protected EditText searchActivity;
    protected Toolbar toolbar;
    protected Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering_data);
        searchBtn = findViewById(R.id.data_search_btn);
        radioGroup2 = findViewById(R.id.radio_group2);
        searchActivity = findViewById(R.id.search_data);
        dataSiteOwner = findViewById(R.id.data_your_site);
        dataSiteParticipants = findViewById(R.id.data_you_join);
        onClickSearch();
        setToolbar();
        setToolbarBackBtn();
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.getMenu().clear();
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }
    }

    private void setToolbarBackBtn(){
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilteringData.this,MapsActivity.class);
                startActivity(intent);
            }
        });
    }


    public void onClickSearch(){
        Intent intent = new Intent(this,MapsActivity.class);
        setResult(RESULT_OK,intent);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((searchActivity.getText().toString().equals("")) || ((radioGroup2.getCheckedRadioButtonId() == -1))) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilteringData.this);
                    alertDialog.setTitle("Search keyword missing or check box is not check").setMessage("Please insert your search keyword or check one of the check boxes")
                            .setNegativeButton("Got it", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                } else {
                    if(!(searchActivity.getText().toString().equals("")) && dataSiteOwner.isChecked()){
                        intent.putExtra("data1",searchActivity.getText().toString());
                        intent.putExtra("data2",dataSiteOwner.getText().toString());
                        Log.d("hello",searchActivity.getText().toString()+" "+dataSiteOwner.getText().toString());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    else {
                        intent.putExtra("data1",searchActivity.getText().toString());
                        intent.putExtra("data2",dataSiteParticipants.getText().toString());
                        Log.d("hello",searchActivity.getText().toString()+" "+dataSiteParticipants.getText().toString());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }
            }
        });
    }

}