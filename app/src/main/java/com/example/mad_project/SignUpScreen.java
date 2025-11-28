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

public class SignUpScreen extends AppCompatActivity {

    private static final String TAG = "SignUpScreen";

    EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    Button buttonSignUp;
    TextView textViewLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.name);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirm_password);
        buttonSignUp = findViewById(R.id.signup_button);
        textViewLogin = findViewById(R.id.login_link);
        progressBar = findViewById(R.id.progressBar);

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpScreen.this, LoginScreen.class);
            startActivity(intent);
        });

        buttonSignUp.setOnClickListener(v -> {
            String name, email, password, confirmPassword;
            name = String.valueOf(editTextName.getText());
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            confirmPassword = String.valueOf(editTextConfirmPassword.getText());

            if (TextUtils.isEmpty(name)) {
                Snackbar.make(v, "Enter name", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(email)) {
                Snackbar.make(v, "Enter email", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Snackbar.make(v, "Enter password", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Snackbar.make(v, "Password must be at least 6 characters", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Snackbar.make(v, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Snackbar.make(v, "Account created.", Snackbar.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(v, "Authentication failed: " + task.getException().getMessage(),
                                    Snackbar.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
