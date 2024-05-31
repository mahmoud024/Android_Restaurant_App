package com.example.final_project.Fragments.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.final_project.JavaClasses.Order;
import com.example.final_project.R;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private List<Order> orders;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }

    public OrdersAdapter(List<Order> orders, OnItemClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView orderDateTextView;
        public TextView orderTimeTextView;
        public TextView orderDetailsTextView;
        public ImageView pizzaImageView;
        public TextView pizzaName;

        public ViewHolder(View view) {
            super(view);

            orderDateTextView = view.findViewById(R.id.orderDateTextView);
            orderTimeTextView = view.findViewById(R.id.orderTimeTextView);
            orderDetailsTextView = view.findViewById(R.id.orderDetailsTextView);
            pizzaImageView = view.findViewById(R.id.pizzaImageView);
            pizzaName = view.findViewById(R.id.pizzaName);
        }

        public void bind(final Order order, final OnItemClickListener listener) {
            // Convert byte array to Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(order.getImageData(), 0, order.getImageData().length);
            pizzaImageView.setImageBitmap(bitmap);

            orderDateTextView.setText(order.getDate());
            orderTimeTextView.setText(order.getTime());
            orderDetailsTextView.setText("Size: " + order.getSize() + ", Quantity: " + order.getQuantity() + ", Price: $" + order.getPrice());
            pizzaName.setText(order.getName());
            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}

