package com.example.final_project.Splash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.Fragments.CatagoriesFragment;
import com.example.final_project.JavaClasses.PizzaAttributes;
import com.example.final_project.R;
import com.example.final_project.RestApi.ConnectionAsyncTask;
import com.example.final_project.RestApi.PizzaTypeJsonParser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getStarted extends AppCompatActivity {

    private static final String BASE_URL = "https://18fbea62d74a40eab49f72e12163fe6c.api.mockbin.io/";
    private DatabaseHelper dbHelper;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        dbHelper = new DatabaseHelper(this);
        progressBar = findViewById(R.id.progressBar); // Find the ProgressBar

        Button getStartedButton = findViewById(R.id.button_get_started);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPizzaTypes();
            }
        });
    }

    private void fetchPizzaTypes() {
        progressBar.setVisibility(View.VISIBLE); // Show the ProgressBar

        String url = BASE_URL;
        new ConnectionAsyncTask(this, new ConnectionAsyncTask.OnDataReceivedListener() {
            @Override
            public void onDataReceived(String data) {
                progressBar.setVisibility(View.GONE); // Hide the ProgressBar

                if (data != null && !data.isEmpty()) {
                    List<String> pizzaTypes = PizzaTypeJsonParser.getPizzaTypesFromJson(data);
                    if (pizzaTypes != null) {
                        insertPizzaDataIntoDB(pizzaTypes);
                        // Connection successful, go to login and registration section
                        Intent intent = new Intent(getStarted.this, StartPage.class);
                        startActivity(intent);
                        // Show success message
                        Toast.makeText(getStarted.this, "Pizza types loaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Show error message
                        Toast.makeText(getStarted.this, "Failed to load pizza types", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Show error message
                    Toast.makeText(getStarted.this, "No data received", Toast.LENGTH_LONG).show();
                }
            }
        }).execute(url);
    }

    private void insertPizzaDataIntoDB(List<String> pizzaTypes) {
        // Define a map with pizza types and their corresponding attributes
        Map<String, PizzaAttributes> pizzaAttributesMap = new HashMap<>();
        pizzaAttributesMap.put("Margarita", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a1), 8, "15 mins", "Cheese, Mushrooms, Beef, Tomato"));
        pizzaAttributesMap.put("Neapolitan", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a2), 9, "14 mins", "Cheese, Mushrooms, Olive Oil, Tomato"));
        pizzaAttributesMap.put("Hawaiian", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a3), 11, "18 mins", "Cheese, Tomato, Ham, Pineapple"));
        pizzaAttributesMap.put("Pepperoni", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a4), 10, "12 mins", "Cheese, Beef, Pepperoni, Veggies"));
        pizzaAttributesMap.put("New York Style", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a5), 12, "16 mins", "Cheese, Tomato, Oregano, Veggies"));
        pizzaAttributesMap.put("Calzone", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a6), 13, "20 mins", "Cheese, Mushrooms, Ham, Tomato"));
        pizzaAttributesMap.put("Tandoori Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a7), 14, "22 mins", "Cheese, Tomato, Chicken, Onions"));
        pizzaAttributesMap.put("BBQ Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a8), 12, "20 mins", "Cheese, BBQ Sauce, Chicken, Onions"));
        pizzaAttributesMap.put("Seafood Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a9), 15, "25 mins", "Cheese, Tomato, Mushrooms, Mussels"));
        pizzaAttributesMap.put("Vegetarian Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a10), 10, "18 mins", "Cheese, Tomato, Bell Peppers, Onions, Olives"));
        pizzaAttributesMap.put("Buffalo Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a11), 13, "22 mins", "Cheese, Beef Sauce, Chicken, Celery"));
        pizzaAttributesMap.put("Mushroom Truffle Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a12), 16, "24 mins", "Cheese, Tomato, Mushrooms, Veggies"));
        pizzaAttributesMap.put("Pesto Chicken Pizza", new PizzaAttributes(convertDrawableToByteArray(R.drawable.a13), 14, "20 mins", "Cheese, Pesto Sauce, Chicken, Tomatoes"));

        for (String pizzaType : pizzaTypes) {
            // Check if the pizza already exists in the database
            if (!dbHelper.isPizzaExists(pizzaType)) {
                PizzaAttributes attributes = pizzaAttributesMap.get(pizzaType);
                if (attributes != null) {
                    dbHelper.insertPizza(pizzaType, attributes.image, attributes.price, attributes.duration, attributes.ingredients);
                } else {
                    // Insert a default pizza if attributes are not defined
                    dbHelper.insertPizza(pizzaType, null, 10, "10 mins", "Cheese, Tomato");
                }
            }
        }
    }

    private byte[] convertDrawableToByteArray(int drawableId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableId);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true); // Resize to 100x100 pixels
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}



