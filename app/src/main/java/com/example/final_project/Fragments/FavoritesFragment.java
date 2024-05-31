package com.example.final_project.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.final_project.DataBase.DatabaseHelper;
import com.example.final_project.SharedPreference.SharedPrefManager;
import com.example.final_project.Fragments.Adapters.FavoritePizzaAdapter;
import com.example.final_project.MainActivity;
import com.example.final_project.R;

import java.util.List;

public class FavoritesFragment extends Fragment implements FavoritePizzaAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private FavoritePizzaAdapter adapter;
    private DatabaseHelper dbHelper;
    private SharedPrefManager sharedPrefManager;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getActivity());
        sharedPrefManager = new SharedPrefManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new FavoritePizzaAdapter(getFavoritePizzas(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private List<FavoritePizzaAdapter.Pizza> getFavoritePizzas() {
        return dbHelper.getFavoritePizzas(sharedPrefManager.getEmail());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Favorites");
        }
    }

    @Override
    public void onItemClick(FavoritePizzaAdapter.Pizza pizza) {
        // Get the user's email from SharedPreferences
        String userEmail = sharedPrefManager.getEmail();
        // Open PizzaDetailsFragment for the selected pizza
        PizzaDetailsFragment pizzaDetailsFragment = PizzaDetailsFragment.newInstance(pizza.getName(), userEmail);
        pizzaDetailsFragment.show(getActivity().getSupportFragmentManager(), "PizzaDetailsFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}



