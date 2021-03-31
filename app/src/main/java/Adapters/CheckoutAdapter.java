package Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.R;
import java.util.ArrayList;
import Model.Cart;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Cart> cartArrayList;

    public CheckoutAdapter(ArrayList<Cart> cartArrayList) {
        this.cartArrayList = cartArrayList;
    }

    @NonNull
    @Override
    public CheckoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutAdapter.ViewHolder holder, final int position) {
        holder.setPrice(String.valueOf(cartArrayList.get(position).getItem().getPrice()));
        holder.setQuantity(String.valueOf(cartArrayList.get(position).getItem().getStockAmount()));
        holder.setTitle(cartArrayList.get(position).getItem().getTitle());
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView totalPrice,quantity,title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cartTitle);
            totalPrice = itemView.findViewById(R.id.cartTotal);
            quantity = itemView.findViewById(R.id.cartQuantity);
        }

        @SuppressLint("SetTextI18n")
        public void setPrice(String p) {
            totalPrice.setText(p + " Euros");
        }
        @SuppressLint("SetTextI18n")
        public void setQuantity(String q) {
           quantity.setText(q + " Qty");
        }
        @SuppressLint("SetTextI18n")
        public void setTitle(String t) {
            title.setText("Title: " + t);
        }
    }
}
