package com.securityCode.splashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Balance_upgrade extends AppCompatActivity {
    Button Update_bal,cancel_btn;
    TextInputEditText New_amount;
    private TextView currentTv;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String Uid;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_upgrade);

        Update_bal=findViewById(R.id.bal_update);
        New_amount=findViewById(R.id.New_amount);
        Uid= FirebaseAuth.getInstance().getUid();
        currentTv=findViewById(R.id.TvCurrent);
        cancel_btn=findViewById(R.id.btn_cancel);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat simpleD = new SimpleDateFormat("MMMyyyy");
        String monthId = simpleD.format(cal.getTime());
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users/"+Uid+"/expenseList/"+monthId+"/New Balance/");

        Update_bal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddhhmmssa");
                String expId = simpleDateFormat.format(calendar.getTime());
                currentTv.setText(expId);
                Toast.makeText(Balance_upgrade.this, ": ", Toast.LENGTH_SHORT).show();
//                myRef.child(expId).child(New_amount.getText().toString()).setValue(New_amount.getText().toString());
            }
        });
    }
}