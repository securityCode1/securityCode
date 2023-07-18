package com.securityCode.splashscreen;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText email_ed,password_ed;
    Button login_btn,signup_btn;
    Dialog progressDialog;
    LinearProgressIndicator progressIndicator;
    TextView textView_progress;
    TextView tv;

    private KeyStore keyStore;

    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "SecurityCode";

    private Cipher cipher;
    private FirebaseAuth mAuth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        progressDialog=new Dialog(LoginActivity.this);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        textView_progress=progressDialog.findViewById(R.id.tv_progress);
        progressIndicator=progressDialog.findViewById(R.id.pd_bar);
        email_ed=findViewById(R.id.email_id);
        password_ed=findViewById(R.id.pass_id);
        login_btn= findViewById(R.id.login_btn);
        signup_btn=findViewById(R.id.signup_btn);
        tv=(TextView) findViewById(R.id.tv1);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=email_ed.getText().toString(),password=password_ed.getText().toString();
                if(email.equals("")||password.equals("")){
                    email_ed.setError("Required");
                    password_ed.setError("Required");
                }else {
                    signIn(email, password);
                }
            }
        });
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent secondActivityIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(secondActivityIntent);
                finish();
            }
        });
        // FingerPrint Auth is added in app uncomment to view demo
//        // Initializing both Android Keyguard Manager and Fingerprint Manager
//        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//        FingerprintManager fingerprintManager = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//        }
//
//        //Find id of Error text
//        TextView errorText = (TextView) findViewById(R.id.error_message);
//
//
//        // Check whether the device has a Fingerprint sensor.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!fingerprintManager.isHardwareDetected()) {
//                /**
//                 * An error message will be displayed if the device does not contain the fingerprint hardware.
//                 * However if you plan to implement a default authentication method,
//                 * you can redirect the user to a default authentication activity from here or can skip this method.
//                 * Example:
//                 * Intent intent = new Intent(this, YourActivity.class);
//                 * startActivity(intent);
//                 * finish();
//                 */
//                errorText.setText(getResources().getString(R.string.fingerprint_not_exist));
//            } else {
//                // Checks whether fingerprint permission is set on manifest
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//                    //If permission is not set show error message
//                    errorText.setText(getResources().getString(R.string.fingerprint_not_enabled));
//                } else {
//                    // Check whether at least one fingerprint is registered on your device
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        if (!fingerprintManager.hasEnrolledFingerprints()) {
//                            //If no fingerprint is registered show error message
//                            errorText.setText(getResources().getString(R.string.fingerprint_not_registered));
//                        } else {
//                            // Checks whether lock screen security is enabled or not
//                            if (!keyguardManager.isKeyguardSecure()) {
//                                //Show error message when screen security is disabled
//                                errorText.setText(getResources().getString(R.string.lock_screen_setting_disabled));
//                            } else {
//
//                                //else generate keystore key
//                                generateKey();
//
//                                //Now initiate Cipher, if cipher is initiated successfully then proceed
//                                if (cipherInit()) {
//                                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
//                                    FingerPrintHandler helper = new FingerPrintHandler(this, errorText);//Set Fingerprint Handler class
//                                    helper.startAuth(fingerprintManager, cryptoObject);//now start authentication process
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
    // Below OnStart method will check if user is already signedIn or not
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
          Toast toast=  Toast.makeText(LoginActivity.this, "success: "+currentUser.getUid(), Toast.LENGTH_SHORT);
            startActivity(new Intent(LoginActivity.this, HomePage.class));
            finish();
        }
    }
    public void signIn(String email,String password){
        progressDialog.show();
        textView_progress.setText("Signing In");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressIndicator.setProgress(90);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "success: "+user.getUid(), Toast.LENGTH_SHORT).show();
                            Intent secondActivityIntent = new Intent(LoginActivity.this, HomePage.class);
                            startActivity(secondActivityIntent);
                            finish();
                            progressDialog.dismiss();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            tv.setText(""+ task.getException());
                            progressDialog.dismiss();
//                            updateUI(null);
                        }
                    }
                });
    }
    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            // Get the reference to the key store
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            // Key generator to generate the key
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException
                 | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException |
                 InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}