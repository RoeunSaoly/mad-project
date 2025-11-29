package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    private static final String TAG = "SignUpScreen";

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonSignUp;
    private TextView textViewLogin;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        initFirebase();
        initViews();
        setupListeners();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void initViews() {
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonSignUp = findViewById(R.id.signup_button);
        textViewLogin = findViewById(R.id.login_link);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        textViewLogin.setOnClickListener(v -> {
            // Navigate to Login screen
            startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
        });

        buttonSignUp.setOnClickListener(v -> attemptSignUp());
    }

    private void attemptSignUp() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (!isInputValid(name, email, password, confirmPassword)) {
            return;
        }

        setInProgress(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            saveUserProfile(firebaseUser, name, email);
                        } else {
                            setInProgress(false);
                            showSnackbar("Failed to get user after creation.");
                        }
                    } else {
                        setInProgress(false);
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        showSnackbar("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private boolean isInputValid(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            showSnackbar("Enter name");
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            showSnackbar("Enter email");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showSnackbar("Enter password");
            return false;
        }
        if (password.length() < 6) {
            showSnackbar("Password must be at least 6 characters");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showSnackbar("Passwords do not match");
            return false;
        }
        return true;
    }

    private void saveUserProfile(FirebaseUser firebaseUser, String name, String email) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("email", email);

        db.collection("users").document(firebaseUser.getUid())
                .set(userProfile)
                .addOnCompleteListener(profileTask -> {
                    if (profileTask.isSuccessful()) {
                        Log.d(TAG, "User profile saved.");
                        showSnackbar("Account created.");
                    } else {
                        Log.w(TAG, "Error saving user profile.", profileTask.getException());
                        showSnackbar("Account created but failed to save profile.");
                    }
                    // Navigate to Home in either case, as Auth was successful
                    navigateToHome();
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            buttonSignUp.setEnabled(true);
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show();
    }
}
