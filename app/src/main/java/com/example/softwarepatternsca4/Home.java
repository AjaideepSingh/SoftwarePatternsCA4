package com.example.softwarepatternsca4;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import Adapters.CatalogueAdapter;
import Authentication.LogIn;
import Model.Item;
import Model.User;

public class Home extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private final ArrayList<Item> items = new ArrayList<>();
    private CatalogueAdapter catalogueAdapter;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private final ArrayList<Item> filteredList = new ArrayList<>();
    private Button filter;
    private String filterType;
    private String objectVariable;
    private EditText search;

    @SuppressLint({"WrongConstant", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.homeToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Catalogue");
        recyclerView = findViewById(R.id.catalogueRCV);
        drawerLayout = findViewById(R.id.a);
        ImageView cartImage = findViewById(R.id.cart);
        cartImage.setOnClickListener(v -> startActivity(new Intent(Home.this, Checkout.class)));
        navigationView = findViewById(R.id.nav_view);
        search = findViewById(R.id.filterHome);
        filter = findViewById(R.id.filter);
        getUserDetailsToPopulateHeader();
        navigationView.setItemIconTintList(null);
        checkUserRights();
        getAllItems();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (Item item : items) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase()) || item.getCategory().toLowerCase().contains(text.toLowerCase()) || item.getManufacturer().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if(!filteredList.isEmpty()) {
            catalogueAdapter.filteredList(filteredList);
        }
        if(!filteredList.isEmpty()) {
            filter.setVisibility(View.VISIBLE);
        }
        if(TextUtils.isEmpty(search.getText().toString())) {
            filter.setVisibility(View.INVISIBLE);
        }
        filter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                    ArrayList<String> filters = new ArrayList<>();
                    ArrayList<String> objectVariables = new ArrayList<>();
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Home.this);
                    @SuppressLint("InflateParams")
                    View view = getLayoutInflater().inflate(R.layout.filterdialogue, null);
                    Spinner filterSpinner = view.findViewById(R.id.spinner4);
                    Spinner objectVariableSpinner = view.findViewById(R.id.spinner5);
                    filters.add("Select Filter!");
                    filters.add("Descending");
                    filters.add("Ascending");
                    objectVariables.add("Select Filtering Type!");
                    objectVariables.add("Category");
                    objectVariables.add("Title");
                    objectVariables.add("Price");
                    objectVariables.add("Manufacturer");
                    objectVariables.add("Stock count");
                    ArrayAdapter filterAdapter = new ArrayAdapter(Home.this, android.R.layout.simple_spinner_dropdown_item, filters) {
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
                    filterSpinner.setAdapter(filterAdapter);
                    filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            filterType = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    ArrayAdapter objectVariableAdapter = new ArrayAdapter(Home.this, android.R.layout.simple_spinner_dropdown_item, objectVariables) {
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
                    objectVariableSpinner.setAdapter(objectVariableAdapter);
                    objectVariableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    objectVariableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            objectVariable = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    builder.setPositiveButton("Create", (dialog, which) -> {});
                    builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                    builder.setView(view);
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                        if (filterSpinner.getSelectedItemPosition() == 0 || objectVariableSpinner.getSelectedItemPosition() == 0) {
                            android.app.AlertDialog.Builder dlgAlert = new android.app.AlertDialog.Builder(Home.this);
                            dlgAlert.setMessage("Select Drop downs");
                            dlgAlert.setTitle("Error...");
                            dlgAlert.setPositiveButton("OK", null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        } else {
                            dialog.dismiss();
                            if(filterType.equals("Descending") && objectVariable.equals("Title")) {
                                Collections.sort(filteredList, Comparator.comparing(Item::getTitle));
                                Collections.reverse(filteredList);
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Descending") && objectVariable.equals("Category")) {
                                Collections.sort(filteredList, Comparator.comparing(Item::getTitle));
                                Collections.reverse(filteredList);
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Descending") && objectVariable.equals("Manufacturer")) {
                                Collections.sort(filteredList, Comparator.comparing(Item::getTitle));
                                Collections.reverse(filteredList);
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Descending") && objectVariable.equals("Price")) {
                                filteredList.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
                                Log.i("fl","" + filteredList.toString());
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Descending") && objectVariable.equals("Stock count")) {
                                filteredList.sort((o1, o2) -> Integer.compare(o2.getStockAmount(), o1.getStockAmount()));
                                catalogueAdapter.filteredList(filteredList);
                            }

                            if(filterType.equals("Ascending") && objectVariable.equals("Title")) {
                                Collections.sort(filteredList, Comparator.comparing(Item::getTitle));
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Ascending") && objectVariable.equals("Category")) {
                                Collections.sort(filteredList, Comparator.comparing(Item::getTitle));
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Ascending") && objectVariable.equals("Manufacturer")) {
                                Collections.sort(filteredList, Comparator.comparing(Item::getTitle));
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Ascending") && objectVariable.equals("Price")) {
                                filteredList.sort((o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
                                Collections.reverse(filteredList);
                                Log.i("fla","" + filteredList.toString());
                                catalogueAdapter.filteredList(filteredList);
                            }
                            if(filterType.equals("Ascending") && objectVariable.equals("Stock count")) {
                                filteredList.sort((o1, o2) -> Integer.compare(o2.getStockAmount(), o1.getStockAmount()));
                                Collections.reverse(filteredList);
                                catalogueAdapter.filteredList(filteredList);
                            }
                        }
                    });
                }
        });
    }

    public void getUserDetailsToPopulateHeader() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    TextView navUsername = headerView.findViewById(R.id.navigationDrawerName);
                    navUsername.setText("User: " + user.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllItems() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Item");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    items.add(item);
                }
                if (items.isEmpty()) {
                    Toast.makeText(Home.this,"No items available!",Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                    catalogueAdapter = new CatalogueAdapter(items, Home.this);
                    recyclerView.setAdapter(catalogueAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkUserRights() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                navNavigator(user.getAccType());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint({"WrongConstant", "NonConstantResourceId"})
    public void navNavigator(String accountType) {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.settings:
                    startActivity(new Intent(Home.this, Settings.class));
                    break;
                case R.id.stockManager:
                    if (accountType.equalsIgnoreCase("admin")) {
                        startActivity(new Intent(Home.this, StockManager.class));
                    } else {
                        Toast.makeText(Home.this, "Admin access only!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.reviewsScreen:
                    startActivity(new Intent(Home.this, Reviews.class));
                    break;
                case R.id.orderHistory:
                    if (accountType.equalsIgnoreCase("admin")) {
                        startActivity(new Intent(Home.this, Orders.class));
                    } else {
                        Toast.makeText(Home.this, "Admin access only!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.userDetails:
                    if (accountType.equalsIgnoreCase("admin")) {
                        startActivity(new Intent(Home.this, UsersDetails.class));
                    } else {
                        Toast.makeText(Home.this, "Admin access only!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.logOut:
                    mAuth.signOut();
                    startActivity(new Intent(Home.this, LogIn.class));
                    break;
            }
            drawerLayout.closeDrawer(Gravity.START);
            return true;
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            startActivity(new Intent(this,Home.class));
        }
    }
}