package com.example.final_project.Admin.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.final_project.Admin.AdminMainActivity;
import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.R;


public class ViewAllOrdersFragment extends Fragment {

    private LinearLayout ordersLinearLayout;
    private DatabaseHelper databaseHelper;
    private SparseBooleanArray clickedItems; // To keep track of clicked items

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_all_orders, container, false);

        ordersLinearLayout = view.findViewById(R.id.ordersLinearLayout);
        databaseHelper = new DatabaseHelper(getContext());
        clickedItems = new SparseBooleanArray();

        loadOrdersFromDatabase();

        return view;
    }

    private void loadOrdersFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT * FROM orders";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String pizzaName = cursor.getString(2);
            int quantity = cursor.getInt(4);
            String size = cursor.getString(3);
            String time = cursor.getString(7);
            String userEmail = cursor.getString(1);
            String userName = getUserFullName(userEmail); // Assuming you have a method to get user's full name
            String userPhone = getUserPhone(userEmail); // Assuming you have a method to get user's phone number


            // Inflate order item layout
            View orderItemView = LayoutInflater.from(getContext()).inflate(R.layout.order_admin_item, ordersLinearLayout, false);
            CardView cardView = orderItemView.findViewById(R.id.cardView);
            // Set click listener to change background color
            final int position = cursor.getPosition();
            boolean isClicked = clickedItems.get(position, false);

            if (isClicked) {
                cardView.setCardBackgroundColor(getResources().getColor(R.color.True)); // Change to your desired color
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickedItems.get(position, false)) {
                        cardView.setCardBackgroundColor(Color.WHITE); // Return to default color
                        clickedItems.put(position, false);
                    } else {
                        cardView.setCardBackgroundColor(getResources().getColor(R.color.True)); // Change to your desired color
                        clickedItems.put(position, true);
                    }
                }
            });

            // Set pizza details
            TextView pizzaNameTextView = orderItemView.findViewById(R.id.pizzaName);
            pizzaNameTextView.setText(pizzaName);

            TextView orderQuantityTextView = orderItemView.findViewById(R.id.orderquantityTextView);
            orderQuantityTextView.setText("Quantity: " + quantity);

            TextView orderTimeTextView = orderItemView.findViewById(R.id.orderTimeTextView);
            orderTimeTextView.setText("Time: " + time);

            TextView orderSizeTextView = orderItemView.findViewById(R.id.ordersizeTextView);
            orderSizeTextView.setText("Size: " + size);

            TextView orderUserEmailTextView = orderItemView.findViewById(R.id.orderUserEmailTextView);
            orderUserEmailTextView.setText("Email: " + userEmail);

            TextView orderUserNameTextView = orderItemView.findViewById(R.id.orderUserNameTextView);
            orderUserNameTextView.setText("Name: " + userName);

            TextView orderUserPhoneTextView = orderItemView.findViewById(R.id.orderUserPhoneTextView);
            orderUserPhoneTextView.setText("Phone: " + userPhone);

            // Add order item to the linear layout
            ordersLinearLayout.addView(orderItemView);
        }

        cursor.close();
    }

    private String getUserFullName(String userEmail) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT userName FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        String userName = "";
        if (cursor != null && cursor.moveToFirst()) {
            userName = cursor.getString(cursor.getColumnIndexOrThrow("userName"));
            cursor.close();
        }

        return userName;
    }

    private String getUserPhone(String userEmail) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String query = "SELECT phone FROM users WHERE email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{userEmail});

        String userPhone = "";
        if (cursor != null && cursor.moveToFirst()) {
            userPhone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            cursor.close();
        }

        return userPhone;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((AdminMainActivity) getActivity()).setToolbarTitle("Orders");
        }
    }


}
