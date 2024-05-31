package com.example.final_project.Fragments;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.RestApi.ConnectionAsyncTask;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatagoriesFragment extends Fragment {

    private static final String BASE_URL = "https://18fbea62d74a40eab49f72e12163fe6c.api.mockbin.io/";
    private LinearLayout layout;
    private DatabaseHelper dbHelper;
    private String userEmail;
    private SharedPrefManager sharedPrefManager;
    private ProgressBar progressBar; // Add ProgressBar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catagories, container, false);
        layout = view.findViewById(R.id.row1);
        progressBar = view.findViewById(R.id.progressBar); // Initialize ProgressBar

        dbHelper = new DatabaseHelper(getActivity());

        // Add filter icon to the app bar
        setHasOptionsMenu(true); // Ensure fragment has options menu

        // Initialize SharedPrefManager and retrieve the email
        sharedPrefManager = new SharedPrefManager(getActivity());
        userEmail = sharedPrefManager.getEmail(); // Get the email from shared preferences

//        fetchPizzaTypes();
        createPizzaCards();
        return view;
    }


//    private void fetchPizzaTypes() {
//        progressBar.setVisibility(View.VISIBLE); // Show ProgressBar
//        String url = BASE_URL;
//        new ConnectionAsyncTask(getActivity(), new ConnectionAsyncTask.OnDataReceivedListener() {
//            @Override
//            public void onDataReceived(String data) {
//                progressBar.setVisibility(View.GONE); // Hide ProgressBar
//                List<String> pizzaTypes = getPizzaTypesFromJson(data);
//                if (pizzaTypes != null) {
//                    insertPizzaDataIntoDB(pizzaTypes);
//                    createPizzaCards();
//                }
//            }
//        }).execute(url);
//    }
//
//    private List<String> getPizzaTypesFromJson(String json) {
//        List<String> pizzaTypes = new ArrayList<>();
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray typesArray = jsonObject.getJSONArray("types");
//            for (int i = 0; i < typesArray.length(); i++) {
//                pizzaTypes.add(typesArray.getString(i));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return pizzaTypes;
//    }
//
//    private void insertPizzaDataIntoDB(List<String> pizzaTypes) {
//        // Define a map with pizza types and their corresponding attributes
//        Map<String, PizzaAttributes> pizzaAttributesMap = new HashMap<>();
//        pizzaAttributesMap.put("Margarita", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a1), 8, "15 mins", "Cheese, Mushrooms, Beef, Tomato"));
//        pizzaAttributesMap.put("Neapolitan", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a2), 9, "14 mins", "Cheese, Mushrooms, Olive Oil, Tomato"));
//        pizzaAttributesMap.put("Hawaiian", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a3), 11, "18 mins", "Cheese, Tomato, Ham, Pineapple"));
//        pizzaAttributesMap.put("Pepperoni", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a4), 10, "12 mins", "Cheese, Beef, Pepperoni, Veggies"));
//        pizzaAttributesMap.put("New York Style", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a5), 12, "16 mins", "Cheese, Tomato, Oregano, Veggies"));
//        pizzaAttributesMap.put("Calzone", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a6), 13, "20 mins", "Cheese, Mushrooms, Ham, Tomato"));
//        pizzaAttributesMap.put("Tandoori Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a7), 14, "22 mins", "Cheese, Tomato, Chicken, Onions"));
//        pizzaAttributesMap.put("BBQ Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a8), 12, "20 mins", "Cheese, BBQ Sauce, Chicken, Onions"));
//        pizzaAttributesMap.put("Seafood Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a9), 15, "25 mins", "Cheese, Tomato, Mushrooms, Mussels"));
//        pizzaAttributesMap.put("Vegetarian Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a10), 10, "18 mins", "Cheese, Tomato, Bell Peppers, Onions, Olives"));
//        pizzaAttributesMap.put("Buffalo Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a11), 13, "22 mins", "Cheese, Beef Sauce, Chicken, Celery"));
//        pizzaAttributesMap.put("Mushroom Truffle Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a12), 16, "24 mins", "Cheese, Tomato, Mushrooms, Veggies"));
//        pizzaAttributesMap.put("Pesto Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a13), 14, "20 mins", "Cheese, Pesto Sauce, Chicken, Tomatoes"));
//
//        for (String pizzaType : pizzaTypes) {
//            // Check if the pizza already exists in the database
//            if (!dbHelper.isPizzaExists(pizzaType)) {
//                PizzaAttributes attributes = pizzaAttributesMap.get(pizzaType);
//                if (attributes != null) {
//                    dbHelper.insertPizza(pizzaType, attributes.image, attributes.price, attributes.duration, attributes.ingredients);
//                } else {
//                    // Insert a default pizza if attributes are not defined
//                    dbHelper.insertPizza(pizzaType, null, 10, "10 mins", "Cheese, Tomato");
//                }
//            }
//        }
//    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            showFilterPopup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_filter, null);

        RangeSlider priceRangeSlider = popupView.findViewById(R.id.rangeSlider);
        RadioGroup categoryRadioGroup = popupView.findViewById(R.id.categoryRadioGroup);

        // Set up the popup window
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(10);
        popupWindow.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.semi_transparent_gray)));

        // Show the popup window
        popupWindow.showAsDropDown(requireActivity().findViewById(R.id.action_filter));

        // Handle price range selection
        priceRangeSlider.setLabelFormatter(value -> "$" + (int) value);

        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            float minPrice = slider.getValues().get(0);
            float maxPrice = slider.getValues().get(1);
            String category = getCategorySelection(categoryRadioGroup);
            filterPizzaCards(minPrice, maxPrice, category);
        });

        // Handle category selection
        categoryRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            float minPrice = priceRangeSlider.getValues().get(0);
            float maxPrice = priceRangeSlider.getValues().get(1);
            String category = getCategorySelection(categoryRadioGroup);
            filterPizzaCards(minPrice, maxPrice, category);
        });
    }


    private String getCategorySelection(RadioGroup categoryRadioGroup) {
        switch (categoryRadioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_chicken:
                return "Chicken";
            case R.id.radio_beef:
                return "Beef";
            case R.id.radio_veggies:
                return "Veggies";
            case R.id.radio_others:
                return "Mushrooms";
            default:
                return "";
        }
    }

    private void filterPizzaCards(float minPrice, float maxPrice, String category) {
        Cursor cursor = dbHelper.getAllPizzas();
        layout.removeAllViews(); // Clear existing pizza cards

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                byte[] image = cursor.getBlob(1);
                int price = cursor.getInt(2);
                String duration = cursor.getString(3);
                String ingredients = cursor.getString(4);

                // Apply filters
                if ((price >= minPrice && price <= maxPrice) && (category.isEmpty() || ingredients.toLowerCase().contains(category.toLowerCase()))) {
                    View cardView = getLayoutInflater().inflate(R.layout.card_pizza, null);
                    CardView pizzaCard = cardView.findViewById(R.id.pizzaCard);
                    TextView pizzaNameTextView = cardView.findViewById(R.id.pizzaName);
                    TextView pizzaPrice = cardView.findViewById(R.id.pizzaPrice);
                    ImageView pizzaImageView = cardView.findViewById(R.id.pizzaImage);

                    pizzaNameTextView.setText(name);
                    pizzaPrice.setText(String.valueOf(price));

                    if (image != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                        pizzaImageView.setImageBitmap(bitmap);
                    }

                    pizzaCard.setOnClickListener(v -> showDetails(name));

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(20, 20, 20, 20);
                    pizzaCard.setLayoutParams(layoutParams);

                    layout.addView(cardView);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void showDetails(String name) {
        if (getActivity() != null && userEmail != null) {
            // Create a new instance of PizzaDetailsFragment with the selected pizza name and user email
            PizzaDetailsFragment fragment = PizzaDetailsFragment.newInstance(name, userEmail);
            // Show the PizzaDetailsFragment as a bottom sheet dialog
            fragment.show(getActivity().getSupportFragmentManager(), "PizzaDetailsFragment");
        }
    }





    private void createPizzaCards() {
        Cursor cursor = dbHelper.getAllPizzas();
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                byte[] image = cursor.getBlob(1);
                int price = cursor.getInt(2);
                String duration = cursor.getString(3);
                String ingredients = cursor.getString(4);

                View cardView = getLayoutInflater().inflate(R.layout.card_pizza, null);
                CardView pizzaCard = cardView.findViewById(R.id.pizzaCard);
                TextView pizzaNameTextView = cardView.findViewById(R.id.pizzaName);
                TextView pizzaPrice = cardView.findViewById(R.id.pizzaPrice);
                ImageView pizzaImageView = cardView.findViewById(R.id.pizzaImage);

                pizzaNameTextView.setText(name);
                pizzaPrice.setText(String.valueOf(price));

                if (image != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    pizzaImageView.setImageBitmap(bitmap);
                }

                pizzaCard.setOnClickListener(v -> showDetails(name));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 20, 20, 20);
                pizzaCard.setLayoutParams(layoutParams);

                layout.addView(cardView);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Add extra bottom margin to the last card
        if (layout.getChildCount() > 0) {
            View lastCardView = layout.getChildAt(layout.getChildCount() - 1);
            if (lastCardView instanceof CardView) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lastCardView.getLayoutParams();
                layoutParams.setMargins(20, 20, 20, 60);
                lastCardView.setLayoutParams(layoutParams);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Category");
        }
    }

}

