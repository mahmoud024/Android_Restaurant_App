package com.example.final_project.Fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.JavaClasses.Order;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PizzaDetailsFragment extends DialogFragment {

    private static final String ARG_PIZZA_NAME = "pizza_name";
    private static final String ARG_USER_EMAIL = "user_email";
    private String mPizzaName;
    private String mUserEmail; // User's email
    private boolean isFavorite = false;
    private DatabaseHelper dbHelper;
    private ImageView addToFavoritesIcon;
    private SharedPrefManager sharedPrefManager;
    private byte[] imageByteArray;


    public PizzaDetailsFragment() {
        // Required empty public constructor
    }

    public static PizzaDetailsFragment newInstance(String pizzaName, String userEmail) {
        PizzaDetailsFragment fragment = new PizzaDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PIZZA_NAME, pizzaName);
        args.putString(ARG_USER_EMAIL, userEmail); // Pass the user's email
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());

        if (getArguments() != null) {
            mPizzaName = getArguments().getString(ARG_PIZZA_NAME);
            mUserEmail = getArguments().getString(ARG_USER_EMAIL); // Get the user's email
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper = new DatabaseHelper(getActivity());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pizza_details, container, false);

        // Initialize SharedPrefManager
        sharedPrefManager = new SharedPrefManager(getContext());

        // Find the ImageView in the inflated view
        addToFavoritesIcon = view.findViewById(R.id.addToFavoritesIcon);

        // Set the initial icon based on the current favorite status
        updateFavoriteIcon(addToFavoritesIcon);

        // Set the click listener for the ImageView
        addToFavoritesIcon.setOnClickListener(v -> {
            isFavorite = !isFavorite; // Toggle the favorite status
            updateFavoriteIcon(addToFavoritesIcon); // Update the icon
            updateFavoriteStatusInDatabase(isFavorite); // Update the database
        });

        // Set the theme and behavior as a bottom sheet dialog
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

        // Set up the order button
        Button orderButton = view.findViewById(R.id.orderButton);
        orderButton.setOnClickListener(v -> showOrderPopup());

        // Fetch and display the pizza details
        displayPizzaDetails(view);

        return view;
    }

    private void displayPizzaDetails(View view) {
        // Find the views in the layout
        TextView titleTextView = view.findViewById(R.id.title);
        TextView priceTextView = view.findViewById(R.id.price);
        ImageView detailImageView = view.findViewById(R.id.detailImage);
        TextView durationTextView = view.findViewById(R.id.detailTime);
        TextView ingredientsTextView = view.findViewById(R.id.detailIngredients);

        // Fetch the pizza details from the database
        Cursor cursor = dbHelper.getPizzaDetails(mPizzaName);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            byte[] image = cursor.getBlob(1); // Get the image byte array from the cursor
            int price = cursor.getInt(2);
            String duration = cursor.getString(3);
            String ingredients = cursor.getString(4);

            // Set the pizza details in the views
            titleTextView.setText(name);
            priceTextView.setText(String.format("$%d", price));
            durationTextView.setText(duration);
            ingredientsTextView.setText(ingredients);

            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                detailImageView.setImageBitmap(bitmap);
            }

            // Store the image byte array for later use
            imageByteArray = image;

            cursor.close();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme);
        View view = View.inflate(getContext(), R.layout.fragment_pizza_details, null);
        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.gravity = Gravity.BOTTOM;
                window.setAttributes(params);
            }
        }
    }

    private void updateFavoriteIcon(ImageView addToFavoritesIcon) {
        if (isFavorite) {
            addToFavoritesIcon.setImageResource(R.drawable.love1); // Change to red color
        } else {
            addToFavoritesIcon.setImageResource(R.drawable.love2); // Change to default color
        }
    }

    private void updateFavoriteStatusInDatabase(boolean isFavorite) {
        if (dbHelper != null) {
            dbHelper.updateFavoriteStatus(mUserEmail, mPizzaName, isFavorite); // Pass the user's email
        } else {
            Log.e("PizzaDetailsFragment", "DbHelper is null");
        }
    }

    private void updateFavoriteStatusFromDatabase() {
        if (dbHelper != null) {
            isFavorite = dbHelper.isPizzaFavorite(mUserEmail, mPizzaName); // Pass the user's email
            updateFavoriteIcon(addToFavoritesIcon); // Update the icon based on the new favorite status
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle(mPizzaName);
        }
        updateFavoriteStatusFromDatabase(); // Update the favorite status based on the database
    }

    private void showOrderPopup() {
        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.popup_order_details, null);

        // Create the popup window
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // Set transparent background

        // Show the popup window in the center of the screen
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        // Set up the size buttons
        Button sizeSButton = popupView.findViewById(R.id.sizeSButton);
        Button sizeMButton = popupView.findViewById(R.id.sizeMButton);
        Button sizeLButton = popupView.findViewById(R.id.sizeLButton);

        final String[] selectedSize = {"S"}; // Default size is S
        final int[] quantity = {1}; // Default quantity is 1
        final double[] unitPrice = {10.00}; // This will be fetched from the database

        // Fetch the pizza price from the database
        Cursor cursor = dbHelper.getPizzaDetails(mPizzaName);
        if (cursor != null && cursor.moveToFirst()) {
            unitPrice[0] = cursor.getDouble(2);
            cursor.close();
        }

        // Set up the quantity buttons and TextView
        Button decreaseQuantityButton = popupView.findViewById(R.id.decreaseQuantityButton);
        Button increaseQuantityButton = popupView.findViewById(R.id.increaseQuantityButton);
        TextView quantityTextView = popupView.findViewById(R.id.quantityTextView);
        TextView priceTextView = popupView.findViewById(R.id.priceTextView);

        sizeSButton.setOnClickListener(v -> {
            selectedSize[0] = "S";
            updatePrice(quantity[0], unitPrice[0], selectedSize[0], priceTextView);
        });
        sizeMButton.setOnClickListener(v -> {
            selectedSize[0] = "M";
            updatePrice(quantity[0], unitPrice[0], selectedSize[0], priceTextView);
        });
        sizeLButton.setOnClickListener(v -> {
            selectedSize[0] = "L";
            updatePrice(quantity[0], unitPrice[0], selectedSize[0], priceTextView);
        });

        increaseQuantityButton.setOnClickListener(v -> {
            quantity[0]++;
            quantityTextView.setText(String.valueOf(quantity[0]));
            updatePrice(quantity[0], unitPrice[0], selectedSize[0], priceTextView);
        });

        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity[0] > 1) {
                quantity[0]--;
                quantityTextView.setText(String.valueOf(quantity[0]));
                updatePrice(quantity[0], unitPrice[0], selectedSize[0], priceTextView);
            }
        });

        // Set the initial price
        updatePrice(quantity[0], unitPrice[0], selectedSize[0], priceTextView);

        // Set up the submit button inside the popup
        Button submitOrderButton = popupView.findViewById(R.id.submitOrderButton);
        submitOrderButton.setOnClickListener(v -> {
            // Get current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String currentDate = dateFormat.format(new Date());
            String currentTime = timeFormat.format(new Date());

            // Get user email from shared preferences or session management
            String userEmail = sharedPrefManager.getEmail(); // Replace this with your method to get the current user's email

            // Retrieve order details
            String size = selectedSize[0];
            int quantityValue = Integer.parseInt(quantityTextView.getText().toString());
            double price = calculatePrice(unitPrice[0], size, quantityValue);

            // Create and save the order
            Order order = new Order(userEmail, mPizzaName, size, quantityValue, price, currentDate, currentTime, imageByteArray);
            DatabaseHelper db = new DatabaseHelper(getContext());
            db.addOrder(order);

            // Dismiss the popup window
            popupWindow.dismiss();
        });
    }

    private void updatePrice(int quantity, double unitPrice, String size, TextView priceTextView) {
        double price = calculatePrice(unitPrice, size, quantity);
        priceTextView.setText("$" + price);
    }

    private double calculatePrice(double unitPrice, String size, int quantity) {
        double price = unitPrice;
        switch (size) {
            case "M":
                price *= 1.5;
                break;
            case "L":
                price *= 2;
                break;
            default:
                break;
        }
        return price * quantity;
    }

}




