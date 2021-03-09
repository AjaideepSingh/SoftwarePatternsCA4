package com.example.softwarepatternsca4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import Adapters.ManagerCatalogueAdapter;
import Model.Item;

public class StockManager extends AppCompatActivity {
    private RecyclerView recyclerView;
    private final ArrayList<Item> items = new ArrayList<>();
    private ManagerCatalogueAdapter managerCatalogueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_manager);
        Toolbar toolbar = findViewById(R.id.StockManagerToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Stock manager");
        ImageView createStock = findViewById(R.id.post);
        recyclerView = findViewById(R.id.stockManagerRecyclerView);
        showAllStock();
        createStock.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Item");
            databaseReference.addValueEventListener(new ValueEventListener() {
                final ArrayList<String> itemTitles = new ArrayList<>();

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        assert item != null;
                        itemTitles.add(item.getTitle());
                    }
                    createStock(itemTitles);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(StockManager.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void createStock(ArrayList<String> itemTitles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StockManager.this);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.addstockpopup, null);
        EditText title, stock, manufacturer, price;
        Spinner category;
        Button pictureAttach;
        ImageView itemPicture;
        title = view.findViewById(R.id.stockTitle);
        stock = view.findViewById(R.id.stockAmount);
        manufacturer = view.findViewById(R.id.stockManufacturer);
        price = view.findViewById(R.id.price);
        category = view.findViewById(R.id.categorySpinner);
        pictureAttach = view.findViewById(R.id.stockPictureButton);
        itemPicture = view.findViewById(R.id.itemPicture);
        final ArrayList<String> categories = new ArrayList<>();
        categories.add("Select category!");
        categories.add("Electronics");
        categories.add("Household");
        categories.add("Furniture");
        categories.add("Other");
        ArrayAdapter categoryAdapter = new ArrayAdapter(StockManager.this, android.R.layout.simple_spinner_dropdown_item, categories) {
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
        category.setAdapter(categoryAdapter);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pictureAttach.setOnClickListener(v -> { });
        builder.setPositiveButton("Create", (dialog, which) -> { });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            if (TextUtils.isEmpty(title.getText().toString())) {
                title.setError("Field cannot be empty!");
                title.requestFocus();
            } else if (TextUtils.isEmpty(manufacturer.getText().toString())) {
                manufacturer.setError("Field cannot be empty!");
                manufacturer.requestFocus();
            } else if (category.getSelectedItemPosition() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(StockManager.this);
                dlgAlert.setMessage("Select category!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else if (TextUtils.isEmpty(price.getText().toString())) {
                price.setError("Field cannot be empty!");
                price.requestFocus();
            } else if (TextUtils.isEmpty(stock.getText().toString())) {
                stock.setError("Field cannot be empty!");
                stock.requestFocus();
            } else if (itemTitles.contains(title.getText().toString())) {
                title.setError("Error this item already exists");
                title.requestFocus();
            } else {
                dialog.dismiss();
                Item item = new Item(title.getText().toString(),manufacturer.getText().toString(),category.getSelectedItem().toString(),"",Double.parseDouble(price.getText().toString()),Integer.parseInt(stock.getText().toString()));
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Item").push().setValue(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(StockManager.this, "Item added to catalogue!", Toast.LENGTH_SHORT).show();
                        items.clear();
                        showAllStock();
                    } else {
                        Toast.makeText(StockManager.this, "Error Occurred" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void showAllStock() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Item");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    items.add(item);
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(StockManager.this));
                managerCatalogueAdapter = new ManagerCatalogueAdapter(items, StockManager.this);
                recyclerView.setAdapter(managerCatalogueAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}