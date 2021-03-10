package Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.Home;
import com.example.softwarepatternsca4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Objects;
import Model.Cart;
import Model.Item;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Item> items;
    private final Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public CatalogueAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CatalogueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull CatalogueAdapter.ViewHolder holder, final int position) {
        holder.setTitle(items.get(position).getTitle());
        holder.setCategory(items.get(position).getCategory());
        holder.setPrice(String.valueOf(items.get(position).getPrice()));
        holder.setImage(items.get(position).getImage());
        holder.setManufacturer(items.get(position).getManufacturer());
        holder.setStock(String.valueOf(items.get(position).getStockAmount()));
        holder.options.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.options);
            popup.inflate(R.menu.customeroptions);
            popup.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                switch (item.getItemId()) {
                    case R.id.addToCard:
                        EditText quantity;
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.addtocartpopup, null);
                        quantity = view.findViewById(R.id.quantity);
                        builder.setPositiveButton("add", (dialog, which) -> {
                        });
                        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        builder.setView(view);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Item");
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                        Item item = itemSnapshot.getValue(Item.class);
                                        assert item != null;
                                        if (item.getTitle().equalsIgnoreCase(items.get(position).getTitle())) {
                                            if (TextUtils.isEmpty(quantity.getText().toString())) {
                                                quantity.setError("Field cannot be empty!");
                                                quantity.requestFocus();
                                            } else if (Double.parseDouble(quantity.getText().toString()) > item.getStockAmount()) {
                                                quantity.setError("Only " + item.getStockAmount() + " left!");
                                                quantity.requestFocus();
                                            } else {
                                                dialog.dismiss();
                                                ImageView imageView = ((Home) context).findViewById(R.id.cart);
                                                imageView.setVisibility(View.VISIBLE);
                                                Item itemToAdd = items.get(position);
                                                itemToAdd.setStockAmount(Integer.parseInt(quantity.getText().toString()));
                                                itemToAdd.setPrice(itemToAdd.getPrice() * itemToAdd.getStockAmount());
                                                Cart cart = new Cart(itemToAdd, mAuth.getUid());
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
                                                databaseReference.push().setValue(cart).addOnCompleteListener(task1 -> {
                                                    if(task1.isSuccessful()) {
                                                        Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
                                                        DatabaseReference updateItemDBReference = FirebaseDatabase.getInstance().getReference("Item");
                                                        updateItemDBReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                                                    Item item = itemSnapshot.getValue(Item.class);
                                                                    assert item != null;
                                                                    item.setId(itemSnapshot.getKey());
                                                                    if (item.getTitle().equalsIgnoreCase(items.get(position).getTitle())) {
                                                                        updateItemDBReference.child(item.getId()).child("stockAmount").setValue(item.getStockAmount() - Integer.parseInt(quantity.getText().toString()));
                                                                        items.clear();
                                                                        notifyDataSetChanged();
                                                                        break;
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Toast.makeText(context, "Error occurred: " + Objects.requireNonNull(error.getMessage()), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    } else {
                                                        Toast.makeText(context, "Error occurred: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(context, "Error occurred: " + Objects.requireNonNull(error.getMessage()), Toast.LENGTH_SHORT).show();
                                }
                            });

                        });
                    break;
                }
                return false;
            });
            popup.show();
    });
}

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final TextView title, price, category, manufacturer, options,stock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemPicture);
            title = itemView.findViewById(R.id.itemTitle);
            price = itemView.findViewById(R.id.itemPrice);
            category = itemView.findViewById(R.id.itemCategory);
            manufacturer = itemView.findViewById(R.id.itemManufacturer);
            options = itemView.findViewById(R.id.itemOptions);
            stock = itemView.findViewById(R.id.rcvRowStock);
        }

        public void setImage(String i) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                itemImage.setImageBitmap(bitmap);
            });
        }

        public void setTitle(String t) {
            title.setText("Title: " + t);
        }

        public void setPrice(String p) {
            price.setText(p + " Euros");
        }

        public void setCategory(String c) {
            category.setText("Category: " + c);
        }

        public void setManufacturer(String m) {
            manufacturer.setText("Manufacturer: " + m);
        }

        public void setStock(String s) {
            stock.setText(s + " Left");
        }
    }
}
