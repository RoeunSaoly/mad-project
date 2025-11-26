package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginScreen extends AppCompatActivity {


    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);

        TextView home_btn = findViewById(R.id.home_btn);
        TextView SignUp = findViewById(R.id.SignUp);
        Button Login_btn = findViewById(R.id.Login);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress3);
        editTextPassword = findViewById(R.id.editTextTextPassword);




        home_btn.setOnClickListener(v -> {
            Intent goMainActivity = new Intent(LoginScreen.this, MainActivity.class);
            startActivity(goMainActivity);
        });

        SignUp.setOnClickListener(v -> {
            Intent goSignUp = new Intent(LoginScreen.this, SignUpScreen.class);
            startActivity(goSignUp);
        });

        Login_btn.setOnClickListener(v -> {
            String Email = editTextEmail.getText().toString();
            String Pass = editTextPassword.getText().toString();

            if (Email.isEmpty() || Pass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("Login", "Email: " + Email + ", Password: " + Pass);
            //Use the data to compare with the database
            Intent intent = new Intent(LoginScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}