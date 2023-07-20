package com.securityCode.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Expense extends AppCompatActivity {

    Button btn_expense,btn_confirm;
    Dialog dialog_add_expense;

    MultiAutoCompleteTextView expenseType;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextInputEditText expenseAmount;
    String Uid;
    private TextView currentTV;
    private static final String[] items = new String[]{"Bike Fuel","Electricity Bill","Gas Bill","Car Fuel"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        currentTV = findViewById(R.id.idTVCurrent);
        Uid= FirebaseAuth.getInstance().getUid();
        btn_expense = findViewById(R.id.expense_btn);
        dialog_add_expense = new Dialog(Expense.this);
        dialog_add_expense.setContentView(R.layout.add_expense_dialog);
        dialog_add_expense.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        expenseType=dialog_add_expense.findViewById(R.id.expense_txt);
        btn_confirm=dialog_add_expense.findViewById(R.id.btn_confirm_expense);
        expenseAmount=dialog_add_expense.findViewById(R.id.total_amount);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, items);

        expenseType.setAdapter(adapter);

        expenseType.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleD = new SimpleDateFormat("MMMyyyy");
        String monthId = simpleD.format(cal.getTime());
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+Uid+"/expenseList/"+monthId);


        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddhhmmssa");
                String expId = simpleDateFormat.format(calendar.getTime());
                currentTV.setText(expId);
                Toast.makeText(Expense.this, ": "+expenseType.getText().toString(), Toast.LENGTH_SHORT).show();
                myRef.child(expId).child(expenseType.getText().toString()).setValue(expenseAmount.getText().toString());
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



