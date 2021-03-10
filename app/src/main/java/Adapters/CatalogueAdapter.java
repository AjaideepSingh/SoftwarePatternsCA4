package Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Objects;

import Authentication.SignUp;
import Model.Cart;
import Model.Item;
import Model.Review;

public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private final ArrayList<Item> items;
    private final Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String ratingMeasure;

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
                    case R.id.leaveReview:
                        LayoutInflater reviewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View reviewView = reviewInflater.inflate(R.layout.reviewpopup, null);
                        Spinner rating = reviewView.findViewById(R.id.ratingSpinner);
                        EditText review = reviewView.findViewById(R.id.reviewMessage);
                        ArrayList<String> reviews = new ArrayList<>();
                        reviews.add("Leave rating!");
                        reviews.add("1");
                        reviews.add("2");
                        reviews.add("3");
                        reviews.add("4");
                        reviews.add("5");
                        ArrayAdapter<String> adapterStudent = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, reviews) {
                            @Override
                            public boolean isEnabled(int position) {
                                return position != 0;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView textview = (TextView) view;
                                if (position == 0) {
                                    textview.setTextColor(Color.GRAY);
                                } else {
                                    textview.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };
                        adapterStudent.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        rating.setAdapter(adapterStudent);
                        rating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                ratingMeasure = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        builder.setPositiveButton("add", (rDialog, which) -> {
                        });
                        builder.setNegativeButton("Close", (rDialog, which) -> rDialog.cancel());
                        builder.setView(reviewView);
                        AlertDialog rDialog = builder.create();
                        rDialog.show();
                        rDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                            if (TextUtils.isEmpty(review.getText().toString())) {
                                review.setError("Error field cannot be empty");
                                review.requestFocus();
                            } else if (rating.getSelectedItemPosition() == 0) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                                dlgAlert.setMessage("Rating must be selected!");
                                dlgAlert.setTitle("Error...");
                                dlgAlert.setPositiveButton("OK", null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                            } else {
                                rDialog.dismiss();
                                Review reviewObj = new Review(review.getText().toString().trim(), mAuth.getUid(), items.get(position).getTitle(), ratingMeasure);
                                DatabaseReference reviewReference = FirebaseDatabase.getInstance().getReference("Review");
                                reviewReference.push().setValue(reviewObj).addOnCompleteListener(task1 -> {
                                    if(task1.isSuccessful()) {
                                        Toast.makeText(context, "Review created", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error occurred: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                        break;
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
                                                    if (task1.isSuccessful()) {
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final TextView title, price, category, manufacturer, options, stock;

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
