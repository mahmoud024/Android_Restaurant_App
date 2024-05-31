package com.example.final_project.Fragments.Adapters;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.JavaClasses.Order;
import com.example.final_project.MainActivity;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.R;

import java.util.List;

public class OrdersFragment extends Fragment {
        private RecyclerView ordersRecyclerView;
        private OrdersAdapter ordersAdapter;
        private DatabaseHelper databaseHelper;
        private List<Order> orderList;
        private SharedPrefManager sharedPrefManager;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_orders, container, false);

            ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView);
            ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            sharedPrefManager = new SharedPrefManager(getContext());
            databaseHelper = new DatabaseHelper(getContext());
            orderList = databaseHelper.getOrders(sharedPrefManager.getEmail());

            ordersAdapter = new OrdersAdapter(orderList, order -> {
                // Handle order click
                showOrderDetails(order);
            });

            ordersRecyclerView.setAdapter(ordersAdapter);

            return view;
        }

        private void showOrderDetails(Order order) {
            // Display order details in a dialog or new activity
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Order Details");

            String message = "" + order.getName() + "\n" +
                    "Size: " + order.getSize() + "\n" +
                    "Quantity: " + order.getQuantity() + "\n" +
                    "Price: $" + order.getPrice() + "\n" +
                    "Date: " + order.getDate() + "\n" +
                    "Time: " + order.getTime();

            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.show();
        }

    @Override
    public void onResume() {
        super.onResume();
        // Change the toolbar title when the fragment is resumed
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Order");
        }
    }
    }

