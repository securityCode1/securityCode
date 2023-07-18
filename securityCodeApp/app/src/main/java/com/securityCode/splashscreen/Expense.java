package com.securityCode.splashscreen;

import static com.securityCode.splashscreen.R.id.btn_confirm_expense;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

public class Expense extends AppCompatActivity {

    Button btn_expense,btn_confirm;
    Dialog dialog_add_expense;
 String[] items={"Bike Fuel","Electricity Bill","Gas Bill","Car Fuel"};

 AutoCompleteTextView autoCompleteTextView;
 ArrayAdapter<String> adapterItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        btn_expense = findViewById(R.id.expense_btn);
        dialog_add_expense = new Dialog(Expense.this);
        dialog_add_expense.setContentView(R.layout.add_expense_dialog);

        btn_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_expense.show();
            }
        });
        
    }
}