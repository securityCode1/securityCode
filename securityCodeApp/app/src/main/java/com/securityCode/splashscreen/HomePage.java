package com.securityCode.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.UUID;

public class HomePage extends AppCompatActivity {
    Button update_btn,closeDailogButton;
    TextView tokenView,emailView,nameView;
    ImageView out_btn,editProfile,imageUpload;
    Dialog edit_profile_dialog;
    EditText phone_ed,name_ed,nic_ed;

    String UID,emailAddress;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference myRef;

    Global global=new Global();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_page);
        out_btn=findViewById(R.id.signout_btn);
        tokenView=findViewById(R.id.tv_data);
        editProfile=findViewById(R.id.img_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance();

        edit_profile_dialog= new Dialog(HomePage.this);
        edit_profile_dialog.setContentView(R.layout.edit_profile_dialog);
        update_btn=edit_profile_dialog.findViewById(R.id.btn_update);

        phone_ed=edit_profile_dialog.findViewById(R.id.phone_number);
        name_ed=edit_profile_dialog.findViewById(R.id.full_name);
        nic_ed=edit_profile_dialog.findViewById(R.id.nic_number);
        closeDailogButton=edit_profile_dialog.findViewById(R.id.btn_cancel);
        edit_profile_dialog.setCancelable(false);

//        edit_profile_dialog.setCancelable(false);
        emailView=edit_profile_dialog.findViewById(R.id.txt_email);
        imageUpload=edit_profile_dialog.findViewById(R.id.profile_image);
        UID=FirebaseAuth.getInstance().getUid();
        myRef = database.getReference("users/"+UID);
        global.setPicassoImage(global.profileImageURL(UID),editProfile);
        emailAddress=FirebaseAuth.getInstance().getCurrentUser().getEmail();
        nameView=findViewById(R.id.tv_name);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("name").getValue(String.class);
                nameView.setText("Hi! "+value);
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
            }
        });

        emailView.setText("Email ID: "+FirebaseAuth.getInstance().getCurrentUser().getEmail()+"\nUID: "+UID);


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
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageUpload.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage(String uid) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("profiles/"+ uid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(HomePage.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(HomePage.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}