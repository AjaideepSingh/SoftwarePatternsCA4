package com.example.softwarepatternsca4;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;
import Adapters.ManagerCatalogueAdapter;
import Model.Item;

public class StockManager extends AppCompatActivity {
    private RecyclerView recyclerView;
    private final ArrayList<Item> items = new ArrayList<>();
    private ManagerCatalogueAdapter managerCatalogueAdapter;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    private Uri mImageUri;
    private String imageDownloadUrl;
    private ImageView itemPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_manager);
        Toolbar toolbar = findViewById(R.id.StockManagerToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Stock manager");
        ImageView createStock = findViewById(R.id.post);
        recyclerView = findViewById(R.id.stockManagerRecyclerView);
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
        showAllStock();
    }

    public void createStock(ArrayList<String> itemTitles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StockManager.this);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.addstockpopup, null);
        EditText title, stock, manufacturer, price;
        Spinner category;
        Button pictureAttach;
        title = view.findViewById(R.id.stockTitle);
        stock = view.findViewById(R.id.stockAmount);
        manufacturer = view.findViewById(R.id.stockManufacturer);
        price = view.findViewById(R.id.price);
        category = view.findViewById(R.id.categorySpinner);
        pictureAttach = view.findViewById(R.id.stockPictureButton);
        itemPicture = view.findViewById(R.id.stockImage);
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
        pictureAttach.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000);
        });
        builder.setPositiveButton("Create", (dialog, which) -> {
        });
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
                Item item = new Item(title.getText().toString(), manufacturer.getText().toString(), category.getSelectedItem().toString(), Double.parseDouble(price.getText().toString()), imageDownloadUrl,Integer.parseInt(stock.getText().toString()));
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Item").push().setValue(item).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(StockManager.this, "Item added to catalgoue", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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
                    assert item != null;
                    item.setId(itemSnapshot.getKey());
                    items.add(item);
                }
                if (items.isEmpty()) {
                    Toast.makeText(StockManager.this,"No items available!",Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(StockManager.this));
                    managerCatalogueAdapter = new ManagerCatalogueAdapter(items, StockManager.this);
                    recyclerView.setAdapter(managerCatalogueAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(itemPicture);
            uploadImageToFirebase();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageToFirebase() {
        if (mImageUri != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image....");
            pd.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                        imageDownloadUrl = fileReference.getPath();
                    }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Error Occurred!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Error", "" + e.getMessage());
            }).addOnProgressListener(snapshot -> {
                double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage " + (int) progressPercent + " " + "%");
            });
        } else {
            Toast.makeText(StockManager.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(StockManager.this, Home.class));
    }
}