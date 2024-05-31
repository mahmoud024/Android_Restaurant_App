package com.example.final_project.Admin;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.final_project.Admin.Fragments.AddAdminFragment;
import com.example.final_project.Admin.Fragments.AddSpecialOffersFragment;
import com.example.final_project.Admin.Fragments.AdminHomeFragment;
import com.example.final_project.Admin.Fragments.AdminProfileFragment;
import com.example.final_project.Admin.Fragments.ViewAllOrdersFragment;
import com.example.final_project.Admin.Fragments.WalletFragment;
import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.Login_Signup.Login;
import com.example.final_project.R;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private SharedPrefManager sharedPrefManager;
    private Toolbar toolbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Ensure the status bar is visible
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize the toolbar
        toolbar2 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar2);

        // Load the initial fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminHomeFragment())
                    .commit();
        }
        toolbar2 = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar2);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up navigation header with user information
        View headerView = navigationView.getHeaderView(0);
        CircleImageView headerImageView = headerView.findViewById(R.id.circularImageView);
        TextView headerProfileUsername = headerView.findViewById(R.id.nav_header_profileUsername);
        TextView headerProfileEmail = headerView.findViewById(R.id.nav_header_profileEmail);

        sharedPrefManager = new SharedPrefManager(this);
        String email = sharedPrefManager.getEmail();

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getUserDataByEmail(email);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String username = cursor.getString(1);
            String profileEmail = cursor.getString(2);
            int imageColumnIndex = cursor.getColumnIndex("image"); // Assuming "image" is the column name for the image
            byte[] imageBytes = cursor.getBlob(imageColumnIndex);

            headerProfileUsername.setText(name + " " + username);
            headerProfileEmail.setText(profileEmail);

            if (imageBytes != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                headerImageView.setImageBitmap(bitmap);
            } else {
                headerImageView.setImageResource(R.drawable.user); // default image
            }
        }
        cursor.close();
        dbHelper.close();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar2, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdminProfileFragment()).commit();
                break;
            case R.id.nav_add_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddAdminFragment()).commit();
                break;
            case R.id.nav_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewAllOrdersFragment()).commit();
                break;
            case R.id.wallet:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WalletFragment()).commit();
                break;
            case R.id.nav_sales:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddSpecialOffersFragment()).commit();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AdminMainActivity.this, Login.class);
                startActivity(intent);
                finish(); // Close the current activity
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to change the toolbar title
    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
