package com.securityCode.splashscreen;

import static com.securityCode.splashscreen.R.id.btn_confirm_expense;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class Expense extends AppCompatActivity {

    Button btn_expense,btn_confirm;
    Dialog dialog_add_expense;

    MultiAutoCompleteTextView expenseType;
    private static final String[] items = new String[]{"Bike Fuel","Electricity Bill","Gas Bill","Car Fuel"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        btn_expense = findViewById(R.id.expense_btn);
        dialog_add_expense = new Dialog(Expense.this);
        dialog_add_expense.setContentView(R.layout.add_expense_dialog);
        dialog_add_expense.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        expenseType=dialog_add_expense.findViewById(R.id.expense_txt);
        btn_confirm=dialog_add_expense.findViewById(R.id.btn_confirm_expense);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, items);

        expenseType.setAdapter(adapter);

        expenseType.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Expense.this, ": "+expenseType.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        btn_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_expense.show();
            }
        });
        
    }
}