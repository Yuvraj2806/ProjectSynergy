package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDOB, editTextRegisterMobile, editTextRegisterPassword, editTextRegisterConfirmPassword;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Environment Monitoring System");
        }

        Toast.makeText(RegisterActivity.this, "You can register now", Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_fullname);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDOB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password);

        radioGroupRegisterGender = findViewById(R.id.radioGroup_register_gender);
        radioGroupRegisterGender.clearCheck();

        // Set up the register button click listener
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textFullName = editTextRegisterFullName.getText().toString().trim();
                String textEmail = editTextRegisterEmail.getText().toString().trim();
                String textDOB = editTextRegisterDOB.getText().toString().trim();
                String textMobile = editTextRegisterMobile.getText().toString().trim();
                String textPwd = editTextRegisterPassword.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPassword.getText().toString();

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                RadioButton radioButtonRegisterGenderSelected = null;
                String textGender = "";

                if (selectedGenderId != -1) {
                    radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                }

                // Validate all fields
                if (TextUtils.isEmpty(textFullName)) {
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    editTextRegisterDOB.setError("Date of birth is required");
                    editTextRegisterDOB.requestFocus();
                } else if (selectedGenderId == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(textMobile)) {
                    editTextRegisterMobile.setError("Mobile no. is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 10) {
                    editTextRegisterMobile.setError("Mobile no. should be of 10 digits");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    editTextRegisterPassword.setError("Password is required");
                    editTextRegisterPassword.requestFocus();
                } else if (textPwd.length() < 6) {
                    editTextRegisterPassword.setError("Password should be at least 6 characters");
                    editTextRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)) {
                    editTextRegisterConfirmPassword.setError("Password is required");
                    editTextRegisterConfirmPassword.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    editTextRegisterConfirmPassword.setError("Passwords do not match");
                    editTextRegisterPassword.setText("");
                    editTextRegisterConfirmPassword.setText("");
                    editTextRegisterConfirmPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDOB, textMobile, textGender, textPwd);
                }
            }
        });
    }

    private void registerUser(String textFullName, String textEmail, String textDOB, String textMobile, String textGender, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User registered successfully. Verification Email sent", Toast.LENGTH_SHORT).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textDOB, textMobile, textGender);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();

                                // Redirect to LoginActivity
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); // Close RegisterActivity
                            } else {
                                Toast.makeText(RegisterActivity.this, "User registration failed. Please try again", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextRegisterPassword.setError("Your password is too weak. Kindly use a mix of alphabets, numbers, and special characters");
                        editTextRegisterPassword.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextRegisterPassword.setError("Invalid email. Kindly re-enter.");
                        editTextRegisterPassword.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextRegisterPassword.setError("User is already registered with this email. Use another email.");
                        editTextRegisterPassword.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
