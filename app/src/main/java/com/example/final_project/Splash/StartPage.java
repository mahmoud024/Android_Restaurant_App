package com.example.final_project.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.final_project.Login_Signup.Login;
import com.example.final_project.Login_Signup.SignUp;
import com.example.final_project.MainActivity;
import com.example.final_project.R;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Button button1 = findViewById(R.id.login);
        Button button2 = findViewById(R.id.signup);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when Button 1 is clicked
                Intent intent = new Intent(StartPage.this, Login.class);
                startActivity(intent);            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action to perform when Button 2 is clicked
                Intent intent = new Intent(StartPage.this, SignUp.class);
                startActivity(intent);            }
        });

    }
}