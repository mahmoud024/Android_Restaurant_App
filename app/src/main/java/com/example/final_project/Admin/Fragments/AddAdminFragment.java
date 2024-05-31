package com.example.final_project.Admin.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.final_project.Admin.AdminMainActivity;
import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.Login_Signup.Login;
import com.example.final_project.Login_Signup.SignUp;
import com.example.final_project.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddAdminFragment extends Fragment {

    DatabaseHelper databaseHelper;
    TextInputLayout signupEmail, signupPassword, signupConfirmPassword, signupName, signupUserName, signupPhone;
    Button signupButton;
    TextView loginRedirectText;
    AutoCompleteTextView genderSpinner; // Change the type to AutoCompleteTextView
    private CountryCodePicker countryCodePicker;
    private TextInputEditText phoneEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_admin, container, false);

        databaseHelper = new DatabaseHelper(getContext());

        signupName = view.findViewById(R.id.name);
        signupUserName = view.findViewById(R.id.username);
        signupEmail = view.findViewById(R.id.email);

        countryCodePicker = view.findViewById(R.id.country_code_picker);
        phoneEditText = view.findViewById(R.id.phone_edit_text);

        countryCodePicker.registerCarrierNumberEditText(phoneEditText);

        signupPassword = view.findViewById(R.id.password);
        signupConfirmPassword = view.findViewById(R.id.confirmpassword);
        signupButton = view.findViewById(R.id.signupButton);
        genderSpinner = view.findViewById(R.id.genderAutoCompleteTV);

        ArrayAdapter<String> genderOptions = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.genders));
        genderSpinner.setAdapter(genderOptions);

        genderSpinner.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                String confirmPassword = signupConfirmPassword.getEditText().getText().toString(); // Correct this line
                int role = 1; // Role 1 for admin

                if (name.equals("") || userName.equals("") || email.equals("") || phone.equals("") || gender.equals("") || password.equals("")) {
                    Toast.makeText(getContext(), "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(email)) {
                    Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                } else if (!isValidPhoneNumber(phone)) {
                    Toast.makeText(getContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                } else if (!isValidName(name)) {
                    Toast.makeText(getContext(), "Name must be at least 3 characters", Toast.LENGTH_SHORT).show();
                } else if (!isValidPassword(password)) {
                    Toast.makeText(getContext(), "Password must be at least 8 characters and include at least 1 character and 1 number", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkUserEmail = databaseHelper.checkEmail(email);
                    if (!checkUserEmail) {
                        Boolean insert = databaseHelper.insertAdminData(name, userName, email, phone, gender, password);
                        if (insert) {
                            Toast.makeText(getContext(), "Admin added successfully!", Toast.LENGTH_SHORT).show();
                            // Optionally redirect to another activity or fragment
                            clearFields();

                        } else {
                            Toast.makeText(getContext(), "Failed to add admin", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "User already exists! Please login", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    public boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void clearFields() {
        signupName.getEditText().setText("");
        signupUserName.getEditText().setText("");
        signupEmail.getEditText().setText("");
        phoneEditText.setText("");
        signupPassword.getEditText().setText("");
        signupConfirmPassword.getEditText().setText("");
        genderSpinner.setText("");
        countryCodePicker.setFullNumber("");
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

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((AdminMainActivity) getActivity()).setToolbarTitle("Add Admin");
        }
    }
}

