package com.example.final_project.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.JavaClasses.Offer;
import com.example.final_project.JavaClasses.PizzaAttributes;
import com.example.final_project.MainActivity;
import com.example.final_project.R;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.Splash.getStarted;

import java.util.ArrayList;
import java.util.List;


public class DealsFragment extends Fragment {

    private LinearLayout row1; // LinearLayout inside NestedScrollView
    private DatabaseHelper dbHelper;
    private String userEmail;
    private SharedPrefManager sharedPrefManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deals, container, false);
        row1 = view.findViewById(R.id.row1); // Find the LinearLayout
        dbHelper = new DatabaseHelper(getActivity());

        // Add your logic to fetch and display offers here
        getAllOffers();

        sharedPrefManager = new SharedPrefManager(getActivity());
        userEmail = sharedPrefManager.getEmail(); // Get the email from shared preferences

        return view;
    }

    private void getAllOffers() {
        List<Offer> offers = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM offers", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Offer offer = new Offer();
                offer.setId(cursor.getInt(cursor.getColumnIndexOrThrow("offer_id")));
                offer.setDiscount(cursor.getInt(cursor.getColumnIndexOrThrow("discount")));
                offer.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow("start_date")));
                offer.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow("end_date")));
                offer.setPizzaName(cursor.getString(cursor.getColumnIndexOrThrow("pizza_name")));
                PizzaAttributes pizzaAttributes = dbHelper.getPizzaAttributes(offer.getPizzaName());
                if (pizzaAttributes != null) {
                    offer.setImage(pizzaAttributes.image);
                    offer.setOldPrice(pizzaAttributes.price);
                }
                offers.add(offer);
            } while (cursor.moveToNext());
            cursor.close();
        }

        for (Offer offer : offers) {
            View offerView = getLayoutInflater().inflate(R.layout.card_pizzas, null);
            CardView pizzaCard = offerView.findViewById(R.id.pizzaCard);
            ImageView pizzaImage = offerView.findViewById(R.id.pizzaImage);
            TextView pizzaName = offerView.findViewById(R.id.pizzaName);
            TextView pizzaPrice = offerView.findViewById(R.id.pizzaPrice);
            TextView start = offerView.findViewById(R.id.textView);
            TextView end = offerView.findViewById(R.id.textView2);


            pizzaName.setText(offer.getPizzaName());
            start.setText(offer.getStartDate());
            end.setText(offer.getEndDate());
            pizzaPrice.setText(String.valueOf(offer.getDiscount()));

            if (offer.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(offer.getImage(), 0, offer.getImage().length);
                pizzaImage.setImageBitmap(bitmap);
            }

            pizzaCard.setOnClickListener(v -> showDetails(offer.getPizzaName()));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 20, 20, 20);
            pizzaCard.setLayoutParams(layoutParams);

            row1.addView(offerView);
        }
    }

    private void showDetails(String categoryName) {
        if (getActivity() != null) {
            // Create a new instance of PizzaDetailsFragment with the selected pizza name and user email
            PizzaDetailsFragment fragment = PizzaDetailsFragment.newInstance(categoryName, userEmail);
            // Show the PizzaDetailsFragment as a bottom sheet dialog
            fragment.show(getActivity().getSupportFragmentManager(), "PizzaDetailsFragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Deals");
        }
    }

}
