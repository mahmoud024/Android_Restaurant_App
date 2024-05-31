package com.example.final_project.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CountryCodePicker countryCodePicker;
    private TextInputEditText phoneEditText;

    private ImageView imageView;
    private DatabaseHelper dbHelper;
    private SharedPrefManager sharedPrefManager;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageView = view.findViewById(R.id.imageView);
        ImageButton changeImageButton = view.findViewById(R.id.changeImageButton);

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        TextView profileName = view.findViewById(R.id.profileName);
        TextView profileUsername = view.findViewById(R.id.profileUsername);
        TextView profileEmail = view.findViewById(R.id.profileEmail);
        TextView profilePhone = view.findViewById(R.id.profilePhone);
        TextView titleUsername = view.findViewById(R.id.titleUsername);
        TextView titleName = view.findViewById(R.id.titleName);

        EditText editFirstName = view.findViewById(R.id.editFirstName);
        EditText editLastName = view.findViewById(R.id.editLastName);
        EditText editPassword = view.findViewById(R.id.editPassword);
        EditText editConfirmPassword = view.findViewById(R.id.editConfirmPassword);

        countryCodePicker = view.findViewById(R.id.country_code_picker);
        phoneEditText = view.findViewById(R.id.phone_edit_text);

        countryCodePicker.registerCarrierNumberEditText(phoneEditText);

        Button saveButton = view.findViewById(R.id.saveButton);
        Button editButton = view.findViewById(R.id.editButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        LinearLayout editableLayout = view.findViewById(R.id.editableLayout);
        LinearLayout linearLayout = view.findViewById(R.id.linearLayout);
        LinearLayout cancelSave = view.findViewById(R.id.cancelSave);

        sharedPrefManager = new SharedPrefManager(getContext());
        String userEmail = sharedPrefManager.getEmail();

        dbHelper = new DatabaseHelper(getContext());
        Cursor cursor = dbHelper.getUserDataByEmail(userEmail);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0); // Assuming "name" is the first column
            String username = cursor.getString(1); // Assuming "userName" is the second column
            String email = cursor.getString(2); // Assuming "email" is the third column
            String phone = cursor.getString(3); // Assuming "phone" is the fourth column

            profileName.setText(name);
            profileUsername.setText(username);
            titleName.setText(name);
            titleUsername.setText(username);
            profileEmail.setText(email);
            profilePhone.setText(phone);

            int imageColumnIndex = cursor.getColumnIndex("image"); // Assuming "image" is the column name for the image
            if (imageColumnIndex != -1) {
                byte[] imageBytes = cursor.getBlob(imageColumnIndex);
                if (imageBytes != null) {
                    Log.d("ProfileFragment", "Image bytes size: " + imageBytes.length);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.d("ProfileFragment", "Image bytes are null");
                }
            } else {
                Log.d("ProfileFragment", "Image column not found in cursor");
            }
        }
        cursor.close();

        // Set EditText fields with existing user data
        editFirstName.setText(profileName.getText().toString());
        editLastName.setText(profileUsername.getText().toString());

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout.setVisibility(View.GONE);
                editableLayout.setVisibility(View.VISIBLE);
                cancelSave.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = editFirstName.getText().toString();
                String newLastName = editLastName.getText().toString();
                String newPassword = editPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();
                String newPhone = phoneEditText.getText().toString().trim();

                // Password validation
                if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.isEmpty() && !isValidPassword(newPassword)) {
                    Toast.makeText(getContext(), "Password must be at least 8 characters long and include both letters and numbers.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Phone number validation
                String fullPhoneNumber = newPhone.isEmpty() ? profilePhone.getText().toString() : countryCodePicker.getFullNumberWithPlus();
                if (!newPhone.isEmpty() && !isValidPhoneNumber(fullPhoneNumber)) {
                    Toast.makeText(getContext(), "Invalid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update TextViews with new values
                profileName.setText(newFirstName);
                profileUsername.setText(newLastName);
                profilePhone.setText(fullPhoneNumber);

                // Update database with new values
                if (newPassword.isEmpty()) {
                    dbHelper.updateUserDatawithoutpass(userEmail, newFirstName, newLastName, fullPhoneNumber);
                } else {
                    dbHelper.updateUserData(userEmail, newFirstName, newLastName, newPassword, fullPhoneNumber);
                }

                // Change visibility back to original state
                linearLayout.setVisibility(View.VISIBLE);
                editableLayout.setVisibility(View.GONE);
                cancelSave.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the visibility of layouts
                linearLayout.setVisibility(View.VISIBLE);
                editableLayout.setVisibility(View.GONE);
                cancelSave.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }


    public boolean isValidPhoneNumber(String phone) {
        String selectedCountry = countryCodePicker.getSelectedCountryNameCode();
        phone = phoneEditText.getText().toString().replaceAll("\\s+", ""); // Get the number from the input field without spaces

        // Debugging statements
        System.out.println("Selected country: " + selectedCountry);
        System.out.println("Phone number: " + phone);

        // Define patterns for each country
        String palestinePattern = "^5[0-9]{8}$";
        String israelPattern = "^5[0-9]{8}$";

        // Select pattern based on country code
        String phoneNumberPattern;
        if ("PS".equals(selectedCountry)) {
            phoneNumberPattern = palestinePattern;
        } else if ("IL".equals(selectedCountry)) {
            phoneNumberPattern = israelPattern;
        } else {
            phoneNumberPattern = "^5[0-9]{8}$"; // Default pattern (if needed)
        }

        boolean isValid = phone.matches(phoneNumberPattern);
        // Additional debugging statement
        System.out.println("Is valid: " + isValid);

        return isValid;
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);

            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                byte[] imageBytes = getBytes(inputStream);
                inputStream.close(); // Close the stream

                String userEmail = sharedPrefManager.getEmail();

                dbHelper.insertImageByEmail(userEmail, imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Profile");
        }
    }

}

