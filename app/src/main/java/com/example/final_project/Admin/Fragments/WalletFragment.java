package com.example.final_project.Admin.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.JavaClasses.PizzaOrderSummary;
import com.example.final_project.R;

import java.util.ArrayList;
import java.util.List;

public class WalletFragment extends Fragment {

    private TextView walletBalance;
    private ListView transactionListView;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        walletBalance = view.findViewById(R.id.walletBalance);
        transactionListView = view.findViewById(R.id.transactionListView);
        databaseHelper = new DatabaseHelper(getContext());

        displayWalletSummary();

        return view;
    }

    private void displayWalletSummary() {
        // Fetch pizza order summary
        List<PizzaOrderSummary> summaryList = databaseHelper.getPizzaOrderSummary();
        double totalIncome = databaseHelper.getTotalIncome();

        // Update wallet balance
        walletBalance.setText(String.format("$%.2f", totalIncome));

        // Display the summary in the ListView
        List<String> summaryDisplayList = new ArrayList<>();
        for (PizzaOrderSummary summary : summaryList) {
            String displayText = String.format("%s: \n%d pizzas, $%.2f total income\nDate: %s\nTime: %s", summary.getPizzaName(), summary.getOrderQuantity(), summary.getTotalIncome(), summary.getOrderDate(), summary.getOrderTime());
            summaryDisplayList.add(displayText);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, summaryDisplayList);
        transactionListView.setAdapter(adapter);
    }
}

