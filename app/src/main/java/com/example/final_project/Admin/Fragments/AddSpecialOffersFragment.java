package com.example.final_project.Admin.Fragments;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.final_project.Admin.AdminMainActivity;
import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class AddSpecialOffersFragment extends Fragment {

    private EditText offerDiscountEditText;
    private TextView startDateTextView, endDateTextView;
    private Spinner pizzaNameSpinner;
    private Button addOfferButton;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_special_offers, container, false);

        offerDiscountEditText = view.findViewById(R.id.offerDiscountEditText);
        startDateTextView = view.findViewById(R.id.startDateEditText);
        endDateTextView = view.findViewById(R.id.endDateEditText);
        pizzaNameSpinner = view.findViewById(R.id.pizzaNameSpinner);
        addOfferButton = view.findViewById(R.id.addOfferButton);

        databaseHelper = new DatabaseHelper(getContext());

        // Populate spinner with pizza names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, getPizzaNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pizzaNameSpinner.setAdapter(adapter);

        startDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDateTextView);
            }
        });

        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(endDateTextView);
            }
        });

        addOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int discount = Integer.parseInt(offerDiscountEditText.getText().toString().trim());
                String startDate = startDateTextView.getText().toString().trim();
                String endDate = endDateTextView.getText().toString().trim();
                String selectedPizza = pizzaNameSpinner.getSelectedItem().toString().trim();

//                // Get the current price of the selected pizza
//                int currentPrice = databaseHelper.getPizzaPrice(selectedPizza);
//
//                // Calculate the new price after discount
//                int newPrice = currentPrice - (currentPrice * discount / 100);

                // Update the price of the selected pizza in the database
                boolean isPriceUpdated = databaseHelper.updatePizzaPrice(selectedPizza, discount);
                if (isPriceUpdated) {
                    Toast.makeText(getContext(), "Price updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update price", Toast.LENGTH_SHORT).show();
                }

                // Insert offer into database
                boolean isInserted = databaseHelper.insertOffer(discount, startDate, endDate, selectedPizza);
                if (isInserted) {
                    Toast.makeText(getContext(), "Offer added successfully", Toast.LENGTH_SHORT).show();

                    // Clear all fields
                    offerDiscountEditText.setText("");
                    startDateTextView.setText("");
                    endDateTextView.setText("");
                    pizzaNameSpinner.setSelection(0);
                } else {
                    Toast.makeText(getContext(), "Failed to add offer", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }

    private void showDatePickerDialog(final TextView dateTextView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dateTextView.setText(selectedDate);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private List<String> getPizzaNames() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        List<String> pizzaNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM pizza", null);
        if (cursor.moveToFirst()) {
            do {
                pizzaNames.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pizzaNames;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((AdminMainActivity) getActivity()).setToolbarTitle("Add Offer");
        }
    }
}

