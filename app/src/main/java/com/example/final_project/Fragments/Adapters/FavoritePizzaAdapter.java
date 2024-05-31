package com.example.final_project.Fragments.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;

import java.util.List;


public class FavoritePizzaAdapter extends RecyclerView.Adapter<FavoritePizzaAdapter.ViewHolder> {

    private List<Pizza> pizzaList;
    private OnItemClickListener listener;

    public FavoritePizzaAdapter(List<Pizza> pizzaList, OnItemClickListener listener) {
        this.pizzaList = pizzaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pizza, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);
        holder.bind(pizza, listener);
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public static class Pizza {
        private String name;
        private byte[] image;
        private int price;
        private String duration;
        private String ingredients;

        public Pizza(String name, byte[] image, int price, String duration, String ingredients) {
            this.name = name;
            this.image = image;
            this.price = price;
            this.duration = duration;
            this.ingredients = ingredients;
        }

        public String getName() {
            return name;
        }

        public byte[] getImage() {
            return image;
        }

        public int getPrice() {
            return price;
        }

        public String getDuration() {
            return duration;
        }

        public String getIngredients() {
            return ingredients;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Pizza pizza);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pizzaNameTextView;
        private ImageView pizzaImageView;
        private TextView pizzaPriceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pizzaNameTextView = itemView.findViewById(R.id.pizzaName);
            pizzaImageView = itemView.findViewById(R.id.pizzaImage);
            pizzaPriceTextView = itemView.findViewById(R.id.pizzaPrice);
        }

        public void bind(final Pizza pizza, final OnItemClickListener listener) {
            pizzaNameTextView.setText(pizza.getName());
            pizzaPriceTextView.setText(String.valueOf(pizza.getPrice()));

            if (pizza.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(pizza.getImage(), 0, pizza.getImage().length);
                pizzaImageView.setImageBitmap(bitmap);
            } else {
                pizzaImageView.setImageResource(R.drawable.pngegg2); // Use a placeholder image if none exists
            }

            itemView.setOnClickListener(v -> listener.onItemClick(pizza));
        }
    }
}


