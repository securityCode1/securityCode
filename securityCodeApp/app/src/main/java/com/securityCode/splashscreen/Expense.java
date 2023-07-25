package com.securityCode.splashscreen;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Expense extends AppCompatActivity {

    Button btn_expense,btn_confirm,Update_bal,cancel_btn,btn_cancel;
TextView totalbudget;
    TextInputEditText New_amount;
    FirebaseDatabase database;
    DatabaseReference myRef,myRefList;
    String Uid;
    Dialog dialog_add_expense,dialog_add_balance;
    ImageView new_balance;

    MultiAutoCompleteTextView expenseType;

    TextInputEditText expenseAmount;
    ListView lv_expense;

    ArrayAdapter adapter;
    ArrayList<String> arr=new ArrayList<String>();

    private TextView currentTV,currentTv;
    private static final String[] items = new String[]{"Bike Fuel","Electricity Bill","Gas Bill","Car Fuel"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        currentTV = findViewById(R.id.idTVCurrent);
        Uid= FirebaseAuth.getInstance().getUid();
        btn_expense = findViewById(R.id.expense_btn);
        lv_expense=findViewById(R.id.list_expense);
        dialog_add_balance=new Dialog(Expense.this);
        dialog_add_balance.setContentView(R.layout.activity_balance_upgrade);
        dialog_add_balance.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        totalbudget=findViewById(R.id.total_budget);


        Update_bal=dialog_add_balance.findViewById(R.id.bal_update);
        New_amount=dialog_add_balance.findViewById(R.id.New_amount);
        currentTv=dialog_add_balance.findViewById(R.id.TvCurrent);
        cancel_btn=dialog_add_balance.findViewById(R.id.btn_cancel);
        totalbudget=findViewById(R.id.total_budget);
        new_balance=findViewById(R.id.add_newBal);
        dialog_add_expense = new Dialog(Expense.this);
        dialog_add_expense.setContentView(R.layout.add_expense_dialog);
        dialog_add_expense.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        expenseType=dialog_add_expense.findViewById(R.id.expense_txt);
        btn_confirm=dialog_add_expense.findViewById(R.id.btn_confirm_expense);
        expenseAmount=dialog_add_expense.findViewById(R.id.total_amount);
        btn_cancel=dialog_add_expense.findViewById(R.id.btn_cancel);
        final ArrayAdapter<String>[] adapter = new ArrayAdapter[]{new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, items)};

        expenseType.setAdapter(adapter[0]);

        expenseType.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleD = new SimpleDateFormat("MMMyyyy");
        String monthId = simpleD.format(cal.getTime());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+Uid+"/expenseList/"+monthId);
        myRefList = database.getReference("users/"+Uid+"/expenseList/"+monthId+"/Expenses");
        // My top posts by number of stars
        myRefList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
//                    Toast.makeText(Expense.this, ""+postSnapshot.getKey(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Expense.this, ""+postSnapshot.toString(), Toast.LENGTH_SHORT).show();
                    arr.add(postSnapshot.getKey()+"\n"+postSnapshot.getValue().toString());
                    adapter[0] =new ArrayAdapter<>(Expense.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,arr);
                    lv_expense.setAdapter(adapter[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("Total Balance").getValue(String.class);
                totalbudget.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddhhMMssa");
                String expId = simpleDateFormat.format(calendar.getTime());
                Toast.makeText(Expense.this, ": "+expenseType.getText().toString(), Toast.LENGTH_SHORT).show();
                myRef.child("Expenses").child(expId).child(expenseType.getText().toString()).setValue(expenseAmount.getText().toString());
            }
        });
        btn_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_expense.show();
            }
        });

        new_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_balance.show();
                Toast.makeText(Expense.this, "Update Card clicked", Toast.LENGTH_SHORT).show();
            }

        });
        Update_bal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Expense.this, " BALANCE UPDATED:"+New_amount, Toast.LENGTH_SHORT).show();
               myRef.child("Total Balance").setValue(New_amount.getText().toString());
               dialog_add_balance.cancel();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    dialog_add_balance.cancel();

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_expense.cancel();
            }
        });
    }
}