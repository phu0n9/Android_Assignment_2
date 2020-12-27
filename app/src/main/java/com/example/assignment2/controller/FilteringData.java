package com.example.assignment2.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
    protected RadioGroup radioGroup1;
    protected RadioGroup radioGroup2;
    protected Button searchBtn;
    protected RadioButton buttonGroup1;
    protected RadioButton buttonGroup2;
    protected EditText searchActivity;
    protected String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtering_data);
        searchBtn = findViewById(R.id.data_search_btn);
        radioGroup1 = findViewById(R.id.radio_group1);
        radioGroup2 = findViewById(R.id.radio_group2);
        searchActivity = findViewById(R.id.search_data);
        onClickSearch();
    }

    public void onClickSearch(){
        int radioButtonId1 = radioGroup1.getCheckedRadioButtonId();
        int radioButtonId2 = radioGroup2.getCheckedRadioButtonId();
        search = null;
        buttonGroup1 = findViewById(radioButtonId1);
        buttonGroup2 = findViewById(radioButtonId2);

        checkSearchBar();
        searchActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search = s.toString().toLowerCase();
                performSearch();
            }
        });
    }


    public void checkSearchBar(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((buttonGroup1.getText().equals("Site address") || buttonGroup1.getText().equals("Site name") || buttonGroup2.getText().equals("Site you owned") || buttonGroup2.getText().equals("Site you joined"))) {
                    Log.d("hello", "case 1");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilteringData.this);
                    alertDialog.setTitle("Search keyword missing").setMessage("Please insert your search keyword")
                            .setNegativeButton("Got it", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                }
                else if(!buttonGroup1.isChecked() && !buttonGroup2.isChecked()){
                    Log.d("hello", "case 2");
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilteringData.this);
                    alertDialog.setTitle("Check missing").setMessage("Please check one of the boxes")
                            .setNegativeButton("Got it", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
                }
            }
        });
    }

    public void performSearch(){
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hello",buttonGroup1.getText().toString());
                Log.d("hello",buttonGroup2.getText().toString());
//                if(buttonGroup1.getText().equals("Site name")){
//                    Log.d("hello","case 3");
//                }
//                else if (buttonGroup1.getText().equals("Site address")){
//                    Log.d("hello","case 4");
//                }
//                else if(buttonGroup2.getText().equals("Site you owned")){
//                    Log.d("hello","case 5");
//                }
//                else if(buttonGroup2.getText().equals("Site you joined")){
//                    Log.d("hello","case 6");
//                }
//                else if(buttonGroup1.getText().equals("Site name") && buttonGroup2.getText().equals("Site you owned")){
//                    Log.d("hello","case 7");
//                }
//                else if(buttonGroup1.getText().equals("Site name") && buttonGroup2.getText().equals("Site you joined")){
//                    Log.d("hello","case 8");
//                }
//                else if(buttonGroup1.getText().equals("Site address") && buttonGroup2.getText().equals("Site you owned")){
//                    Log.d("hello","case 9");
//                }
//                else if(buttonGroup1.getText().equals("Site address") && buttonGroup2.getText().equals("Site you joined")){
//                    Log.d("hello","case 10");
//                }
//                else if(!buttonGroup1.isChecked() && !buttonGroup2.isChecked()){
//                    Log.d("hello","case 11");
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilteringData.this);
//                    alertDialog.setTitle("Check missing").setMessage("Please check one of the boxes")
//                            .setNegativeButton("Got it", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
//                }
            }
        });
    }
}