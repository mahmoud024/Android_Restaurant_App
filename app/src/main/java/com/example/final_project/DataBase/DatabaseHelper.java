package com.example.final_project.DataBase;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.final_project.JavaClasses.Offer;
import com.example.final_project.JavaClasses.Order;
import com.example.final_project.Fragments.Adapters.FavoritePizzaAdapter;
import com.example.final_project.JavaClasses.PizzaAttributes;
import com.example.final_project.JavaClasses.PizzaOrderSummary;
import com.example.final_project.JavaClasses.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String databaseName = "Signupss.db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 13);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("CREATE TABLE users(name TEXT, userName TEXT, email TEXT PRIMARY KEY, phone TEXT, gender TEXT, password TEXT, image BLOB ,role INTEGER)");

        // Insert static admin data
        ContentValues adminValues = new ContentValues();
        adminValues.put("name", "Admin");
        adminValues.put("userName", "admin");
        adminValues.put("email", "admin@gmail.com");
        adminValues.put("phone", "+970592110309");
        adminValues.put("gender", "Male");
        adminValues.put("password", hashPassword("admin123")); // Hash the password before storin
        adminValues.put("role", 1);
        MyDatabase.insert("users", null, adminValues);

        MyDatabase.execSQL("CREATE TABLE pizza(name TEXT PRIMARY KEY, image BLOB, price INTEGER, duration TEXT, ingredients TEXT)");
        MyDatabase.execSQL("CREATE TABLE user_pizza(user_email TEXT, pizza_name TEXT, isLike INTEGER, PRIMARY KEY(user_email, pizza_name), FOREIGN KEY(user_email) REFERENCES users(email), FOREIGN KEY(pizza_name) REFERENCES pizza(name))");
        MyDatabase.execSQL("CREATE TABLE orders(order_id INTEGER PRIMARY KEY AUTOINCREMENT, user_email TEXT, pizzaname TEXT, size TEXT, quantity INTEGER, price REAL, date TEXT, time TEXT, imageData BLOB, FOREIGN KEY(user_email) REFERENCES users(email))");
        MyDatabase.execSQL("CREATE TABLE offers(offer_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, discount INTEGER, start_date TEXT, end_date TEXT, pizza_name TEXT, FOREIGN KEY(pizza_name) REFERENCES pizza(name))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("drop Table if exists users");
        MyDB.execSQL("drop Table if exists pizza");
        MyDB.execSQL("drop Table if exists user_pizza");
        MyDB.execSQL("drop Table if exists orders");
        MyDB.execSQL("drop Table if exists offers");
        onCreate(MyDB);
    }

    // Insert user data
    public Boolean insertData(String name, String userName, String email, String phone, String gender, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("userName", userName);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("gender", gender);
        contentValues.put("password", hashPassword(password));
        contentValues.put("role", 0); // Add the role field
        long result = MyDatabase.insert("users", null, contentValues);
        return result != -1;
    }

    // Insert user data
    public Boolean insertAdminData(String name, String userName, String email, String phone, String gender, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("userName", userName);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("gender", gender);
        contentValues.put("password", hashPassword(password));
        contentValues.put("role", 1); // Add the role field
        long result = MyDatabase.insert("users", null, contentValues);
        return result != -1;
    }

    public boolean insertOffer(int discount, String startDate, String endDate, String pizzaName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("discount", discount);
        contentValues.put("start_date", startDate);
        contentValues.put("end_date", endDate);
        contentValues.put("pizza_name", pizzaName);
        long result = db.insert("offers", null, contentValues);
        return result != -1;
    }


    public User getUserByEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password); // Ensure you hash the password before checking
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, hashedPassword});

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String username = cursor.getString(1);
            String phone = cursor.getString(3);
            String gender = cursor.getString(4);
            int role = cursor.getInt(7); // Fetching role
            cursor.close();
            return new User(name, username, email, phone, gender, hashedPassword, role); // Return user with role
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // Insert user image by email
    public boolean insertImageByEmail(String email, byte[] image) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("image", image);
        int result = MyDatabase.update("users", contentValues, "email = ?", new String[]{email});
        return result > 0;
    }

    // Check if email exists
    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from users where email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Check email and password
    public boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String hashedPassword = hashPassword(password); // Hash the password before comparing
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, hashedPassword});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Get user data by email
    public Cursor getUserDataByEmail(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        String[] columns = {"name", "userName", "email", "phone", "gender", "password", "image"};
        String selection = "email=?";
        String[] selectionArgs = {email};
        return MyDatabase.query("users", columns, selection, selectionArgs, null, null, null);
    }

    // Hash password using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Update user data
    public void updateUserData(String email, String newFirstName, String newLastName, String newPassword, String newPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", newFirstName);
        contentValues.put("userName", newLastName);
        contentValues.put("password", hashPassword(newPassword));
        contentValues.put("phone", newPhone);

        db.update("users", contentValues, "email = ?", new String[]{email});
    }

    // Update user data without changing password
    public void updateUserDatawithoutpass(String email, String newFirstName, String newLastName, String newPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", newFirstName);
        contentValues.put("userName", newLastName);
        contentValues.put("phone", newPhone);

        db.update("users", contentValues, "email = ?", new String[]{email});
    }

    // Insert pizza details
    public boolean insertPizza(String name, byte[] image, int price, String duration, String ingredients) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("image", image);
        contentValues.put("price", price);
        contentValues.put("duration", duration);
        contentValues.put("ingredients", ingredients);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert("pizza", null, contentValues);
        return result != -1;
    }

    // Retrieve all pizzas
    public Cursor getAllPizzas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM pizza", null);
    }

    // Method to get pizza details
    public Cursor getPizzaDetails(String pizzaName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + "pizza" + " WHERE " + "name" + " = ?";
        return db.rawQuery(query, new String[]{pizzaName});
    }

    public PizzaAttributes getPizzaAttributes(String pizzaName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pizza WHERE name = ?", new String[]{pizzaName});
        PizzaAttributes pizzaAttributes = null;
        if (cursor != null && cursor.moveToFirst()) {
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow("duration"));
            String ingredients = cursor.getString(cursor.getColumnIndexOrThrow("ingredients"));
            pizzaAttributes = new PizzaAttributes(image, price, duration, ingredients);
            cursor.close();
        }
        return pizzaAttributes;
    }

    public int getPizzaPrice(String pizzaName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT price FROM pizza WHERE name = ?", new String[]{pizzaName});
        int price = 0;
        if (cursor != null && cursor.moveToFirst()) {
            price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
            cursor.close();
        }
        return price;
    }

    public boolean updatePizzaPrice(String pizzaName, int newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("price", newPrice);
        int rowsAffected = db.update("pizza", values, "name = ?", new String[]{pizzaName});
        return rowsAffected > 0;
    }


    // Update pizza details
    public boolean updatePizza(String name, byte[] image, int price, String duration, String ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("image", image);
        contentValues.put("price", price);
        contentValues.put("duration", duration);
        contentValues.put("ingredients", ingredients);
        int result = db.update("pizza", contentValues, "name = ?", new String[]{name});
        return result > 0;
    }

    // Delete pizza by name
    public boolean deletePizza(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("pizza", "name = ?", new String[]{name});
        return result > 0;
    }

    // Check if pizza exists
    public boolean isPizzaExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("pizza", new String[]{"name"}, "name=?", new String[]{name}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Update favorite status in the user_pizza table
    public void updateFavoriteStatus(String email, String pizzaName, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isLike", isFavorite ? 1 : 0);

        // Check if the entry exists
        Cursor cursor = db.query("user_pizza", null, "user_email = ? AND pizza_name = ?", new String[]{email, pizzaName}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            // Update existing entry
            db.update("user_pizza", values, "user_email = ? AND pizza_name = ?", new String[]{email, pizzaName});
        } else {
            // Insert new entry
            values.put("user_email", email);
            values.put("pizza_name", pizzaName);
            db.insert("user_pizza", null, values);
        }
        cursor.close();
    }

    // Check if a pizza is a favorite for a specific user
    public boolean isPizzaFavorite(String email, String pizzaName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("user_pizza", new String[]{"isLike"}, "user_email = ? AND pizza_name = ?", new String[]{email, pizzaName}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            boolean isFavorite = cursor.getInt(0) > 0;
            cursor.close();
            return isFavorite;
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    // Get favorite pizzas for a specific user
    public List<FavoritePizzaAdapter.Pizza> getFavoritePizzas(String email) {
        List<FavoritePizzaAdapter.Pizza> favoritePizzas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.name, p.image, p.price, p.duration, p.ingredients " +
                "FROM pizza p JOIN user_pizza up ON p.name = up.pizza_name " +
                "WHERE up.user_email = ? AND up.isLike = 1";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                byte[] image = cursor.getBlob(1);
                int price = cursor.getInt(2);
                String duration = cursor.getString(3);
                String ingredients = cursor.getString(4);

                favoritePizzas.add(new FavoritePizzaAdapter.Pizza(name, image, price, duration, ingredients));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favoritePizzas;
    }

    // Method to add an order
    public void addOrder(Order order) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_email", order.getUserEmail());
        values.put("pizzaname",order.getName());
        values.put("size", order.getSize());
        values.put("quantity", order.getQuantity());
        values.put("price", order.getPrice());
        values.put("date", order.getDate());
        values.put("time", order.getTime());
        values.put("imageData", order.getImageData());

        db.insert("orders", null, values);
        db.close();
    }

    // Method to get all orders for a user
    public List<Order> getOrders(String userEmail) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("orders", null, "user_email" + "=?", new String[]{userEmail}, null, null, "date" + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order(
                        cursor.getString(cursor.getColumnIndexOrThrow("user_email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pizzaname")),
                        cursor.getString(cursor.getColumnIndexOrThrow("size")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("imageData"))
                );
                orderList.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderList;
    }

    public List<PizzaOrderSummary> getPizzaOrderSummary() {
        List<PizzaOrderSummary> summaryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pizza.name, SUM(orders.quantity) AS order_quantity, SUM(orders.price) AS total_income, orders.date, orders.time " +
                "FROM orders " +
                "JOIN pizza ON orders.pizzaname = pizza.name " +
                "GROUP BY pizza.name, orders.date, orders.time";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int orderQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("order_quantity"));
                double totalIncome = cursor.getDouble(cursor.getColumnIndexOrThrow("total_income"));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String orderTime = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                summaryList.add(new PizzaOrderSummary(name, orderQuantity, totalIncome, orderDate, orderTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return summaryList;
    }



    // Method to get total income for all pizzas
    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(price) AS total_income FROM orders";
        Cursor cursor = db.rawQuery(query, null);
        double totalIncome = 0;
        if (cursor.moveToFirst()) {
            totalIncome = cursor.getDouble(cursor.getColumnIndexOrThrow("total_income"));
        }
        cursor.close();
        return totalIncome;
    }

}

