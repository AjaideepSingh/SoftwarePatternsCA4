package Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
        holder.setImage(cartArrayList.get(position).getItem().getImage());
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
        private final ImageView cartImage;
        private final TextView totalPrice;
        private final TextView quantity;
        private final TextView title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cartTitle);
            cartImage = itemView.findViewById(R.id.cartImage);
            totalPrice = itemView.findViewById(R.id.cartTotal);
            quantity = itemView.findViewById(R.id.cartQuantity);
        }


        public void setImage(String i) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                cartImage.setImageBitmap(bitmap);
            });
        }
        public void setPrice(String p) {
            totalPrice.setText(p + " Euros");
        }
        public void setQuantity(String q) {
           quantity.setText(q + " Amount");
        }
        public void setTitle(String t) {
            title.setText("Title: " + t);
        }
    }
}
