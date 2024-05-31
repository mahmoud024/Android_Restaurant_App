package com.example.final_project.Login_Signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    TextInputLayout signupEmail, signupPassword, signupConfirmPassword, signupName, signupUserName, signupPhone;
    Button signupButton;
    TextView loginRedirectText;
    AutoCompleteTextView genderSpinner;
    private CountryCodePicker countryCodePicker;
    private TextInputEditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        databaseHelper = new DatabaseHelper(this);

        signupName = findViewById(R.id.name);
        signupUserName = findViewById(R.id.username);
        signupEmail = findViewById(R.id.email);

        countryCodePicker = findViewById(R.id.country_code_picker);
        phoneEditText = findViewById(R.id.phone_edit_text);

        countryCodePicker.registerCarrierNumberEditText(phoneEditText);

        signupPassword = findViewById(R.id.password);
        signupConfirmPassword = findViewById(R.id.confirmpassword);
        signupButton = findViewById(R.id.signupButton);
        loginRedirectText = findViewById(R.id.loginText);
        genderSpinner = findViewById(R.id.genderAutoCompleteTV);

        ArrayAdapter<String> genderOptions = new ArrayAdapter<>(SignUp.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.genders));
        genderSpinner.setAdapter(genderOptions);

        genderSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        genderSpinner.setKeyListener(null);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signupName.getEditText().getText().toString();
                String userName = signupUserName.getEditText().getText().toString();
                String email = signupEmail.getEditText().getText().toString();
                String phone = getFullPhoneNumber();
                String gender = genderSpinner.getText().toString();
                String password = signupPassword.getEditText().getText().toString();
                String confirmPassword = signupConfirmPassword.getEditText().getText().toString();

                if (name.equals("") || userName.equals("") || email.equals("") || phone.equals("") || gender.equals("") || password.equals("")) {
                    Toast.makeText(SignUp.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(SignUp.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(SignUp.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                } else if (!isValidName(name)) {
                    Toast.makeText(SignUp.this, "Name must be at least 3 characters", Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(password)) {
                    Toast.makeText(SignUp.this, "Password must be at least 8 characters and include at least 1 character and 1 number", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkUserEmail = databaseHelper.checkEmail(email);
                    if (!checkUserEmail) {
                        Boolean insert = databaseHelper.insertData(name, userName, email, phone, gender, password);
                        if (insert) {
                            Toast.makeText(SignUp.this, "Signup Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignUp.this, "Signup Failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "User already exists! Please login", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isValidPhoneNumber(String phone) {
        String selectedCountry = countryCodePicker.getSelectedCountryNameCode();
        phone = phoneEditText.getText().toString().replaceAll("\\s+", "");

        String palestinePattern = "^5[0-9]{8}$";
        String israelPattern = "^5[0-9]{8}$";

        String phoneNumberPattern;
        if ("PS".equals(selectedCountry)) {
            phoneNumberPattern = palestinePattern;
        } else if ("IL".equals(selectedCountry)) {
            phoneNumberPattern = israelPattern;
        } else {
            phoneNumberPattern = "^5[0-9]{8}$";
        }

        return phone.matches(phoneNumberPattern);
    }

    public String getFullPhoneNumber() {
        return countryCodePicker.getFullNumberWithPlus();
    }

    public boolean isValidName(String name) {
        return name.length() >= 3;
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }
}



