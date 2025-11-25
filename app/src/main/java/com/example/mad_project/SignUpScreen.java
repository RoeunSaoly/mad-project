package com.example.mad_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpScreen extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirm;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_screen);

        TextView Login_btn = findViewById(R.id.Login_btn);
        Button SignUp_btn = findViewById(R.id.SignUp_btn);
        editTextName = findViewById(R.id.editTextTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);



        Login_btn.setOnClickListener(v -> finish());

        SignUp_btn.setOnClickListener(v -> {
            String Name = editTextName.getText().toString();
            String Email = editTextEmail.getText().toString();
            String Pass = editTextPassword.getText().toString();
            String PassConfirm = editTextPasswordConfirm.getText().toString();

            if (Name.isEmpty() || Email.isEmpty() || Pass.isEmpty() || PassConfirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;

            }
            if (!Pass.equals(PassConfirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            //Use the data to save in the database
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}