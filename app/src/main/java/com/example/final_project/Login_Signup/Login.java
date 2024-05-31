package com.example.final_project.Login_Signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.Admin.AdminMainActivity;
import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.JavaClasses.User;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    TextInputLayout loginEmail, loginPassword;
    Button loginButton;
    CheckBox rememberMe;
    DatabaseHelper databaseHelper;
    SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);
        sharedPrefManager = new SharedPrefManager(this);

        loginEmail = findViewById(R.id.email);
        loginPassword = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        rememberMe = findViewById(R.id.rememberMe);

        // Set email if remember me is checked
        if (sharedPrefManager.getRememberMe()) {
            loginEmail.getEditText().setText(sharedPrefManager.getEmail());
            rememberMe.setChecked(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getEditText().getText().toString();
                String password = loginPassword.getEditText().getText().toString();
                if (email.equals("") || password.equals("")) {
                    Toast.makeText(Login.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else {
                    User user = databaseHelper.getUserByEmailPassword(email, password);
                    if (user != null) {
                        if (rememberMe.isChecked()) {
                            sharedPrefManager.saveEmail(email);
                            sharedPrefManager.setRememberMe(true);
                        } else {
                            sharedPrefManager.clear();
                        }

                        System.out.println("role ======= "+user.getRole());
                        if (user.getRole() == 1) { // Admin role
                            Toast.makeText(Login.this, "Admin Login Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), AdminMainActivity.class);
                            startActivity(intent);
                        } else if (user.getRole() == 0){ // Customer role
                            Toast.makeText(Login.this, "Customer Login Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(Login.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Go to Signup
        TextView signupText = findViewById(R.id.signupText);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}





