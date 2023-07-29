package com.securityCode.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class HomePage extends AppCompatActivity {
    Button update_btn, closeDailogButton;
    CardView expenseCard,Grant_access,securityCard;
    TextView tokenView, emailView, nameView;
    ImageView out_btn, editProfile, imageUpload;
    Dialog edit_profile_dialog, progressDialog, grantaccessdialog;
    EditText phone_ed, name_ed, nic_ed;
    LinearProgressIndicator progressIndicator;
    TextView textView_progress;
    String UID, emailAddress;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;


    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Global global = new Global();

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String[] type = {"Bike fuel", "Grocery", "Electricity bill", "Gas bill", "Car fuel"};

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        out_btn = findViewById(R.id.signout_btn);
        tokenView = findViewById(R.id.tv_data);
        editProfile = findViewById(R.id.img_profile);
        expenseCard = (CardView) findViewById(R.id.expense_card);
        securityCard=(CardView) findViewById(R.id.securityLog_card);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();
        progressDialog = new Dialog(HomePage.this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        textView_progress = progressDialog.findViewById(R.id.tv_progress);
        progressIndicator = progressDialog.findViewById(R.id.pd_bar);

        edit_profile_dialog = new Dialog(HomePage.this);
        edit_profile_dialog.setContentView(R.layout.edit_profile_dialog);
        edit_profile_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        update_btn = edit_profile_dialog.findViewById(R.id.btn_update);

        phone_ed = edit_profile_dialog.findViewById(R.id.phone_number);
        name_ed = edit_profile_dialog.findViewById(R.id.full_name);
        nic_ed = edit_profile_dialog.findViewById(R.id.nic_number);
        closeDailogButton = edit_profile_dialog.findViewById(R.id.btn_cancel);
        edit_profile_dialog.setCancelable(false);

        Grant_access = (CardView) findViewById(R.id.access_card);
        grantaccessdialog = new Dialog(HomePage.this);
        grantaccessdialog.setContentView(R.layout.grant_access_dialog);
        grantaccessdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        edit_profile_dialog.setCancelable(false);
        emailView = edit_profile_dialog.findViewById(R.id.txt_email);
        imageUpload = edit_profile_dialog.findViewById(R.id.profile_image);
        UID = FirebaseAuth.getInstance().getUid();
        myRef = database.getReference("users/" + UID);
        global.setPicassoImage(global.profileImageURL(UID), editProfile);
        emailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        nameView = findViewById(R.id.tv_name);

        expenseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, Expense.class));
            }
        });

        securityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomePage.this, Securitylog.class));
            }
        });
        Grant_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grantaccessdialog.show();
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String name = dataSnapshot.child("name").getValue(String.class);
                String contact = dataSnapshot.child("contact").getValue(String.class);
                String nic = dataSnapshot.child("nic").getValue(String.class);
                nameView.setText("Hi! " + name);
                name_ed.setText(name);
                phone_ed.setText(contact);
                nic_ed.setText(nic);

                global.setPicassoImage(global.profileImageURL(UID), imageUpload);
//                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
//                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("name").setValue(name_ed.getText().toString());
                myRef.child("email").setValue(emailAddress);
                myRef.child("contact").setValue(phone_ed.getText().toString());
                myRef.child("nic").setValue(nic_ed.getText().toString());
                uploadImage(UID);
                edit_profile_dialog.cancel();
            }
        });

        emailView.setText("Email ID: " + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "\nUID: " + UID);


        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomePage.this, "You wanna edit your profile?", Toast.LENGTH_SHORT).show();
                edit_profile_dialog.show();
            }
        });

        closeDailogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_profile_dialog.cancel();
            }
        });

        out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomePage.this, LoginActivity.class));
                finish();
            }
        });

    }

    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("scale", 25);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageUpload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String uid) {
        if (filePath != null) {
            progressDialog.show();
            StorageReference ref = storageReference.child("profiles/" + uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressIndicator.setProgress(100);
                            textView_progress.setText("Uploaded");
                            progressDialog.dismiss();
                            Toast.makeText(HomePage.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(HomePage.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressIndicator.setProgress((int) progress);
                            textView_progress.setText("Uploaded " + (int) progress + "%");

                        }
                    });


        }

    }
}