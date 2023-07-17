package com.securityCode.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Expense extends AppCompatActivity {
Button btn_expense;
Dialog dialog_add_expense;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        btn_expense=findViewById(R.id.expense_btn);
        dialog_add_expense=new Dialog(Expense.this);
        dialog_add_expense.setContentView(R.layout.add_expense_dialog);

        btn_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_expense.show();
            }
        });
    }
}