package com.example.softwarepatternsca4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private ImageView up,down;
    private final ArrayList<Item> filteredList = new ArrayList<>();
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
        up = findViewById(R.id.ascending);
        down = findViewById(R.id.descending);
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
        catalogueAdapter.filteredList(filteredList);
        if(!filteredList.isEmpty()) {
            up.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
        }
        if(filteredList.isEmpty() || TextUtils.isEmpty(search.getText().toString())) {
            up.setVisibility(View.INVISIBLE);
            down.setVisibility(View.INVISIBLE);
        }

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(filteredList, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        //Log.i("test","" + (int) (o1.getPrice() - o2.getPrice()));
                        Log.i("test1","" + o1.getPrice());
                        Log.i("test2","" + o1.getTitle());
                        Log.i("test3","" + o2.toString());
                        Log.i("test4","" + (int) (o1.getPrice() - o2.getPrice()));

                        for(int i = 0; i < filteredList.size(); i++) {
                            if(!filteredList.get(i).getTitle().equalsIgnoreCase(o1.getTitle())) {
                                filteredList.remove(i);
                            }
                        }
                        Log.i("fl","" + filteredList.toString());
                        return (int) (o1.getPrice() - o2.getPrice());
                    }
                });
            }
        });
//        down.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Collections.sort(filteredList, new Comparator<Item>() {
//                    @Override
//                    public int compare(Item c1, Item c2) {
//                        Log.i("1","" + c1.toString());
//                        Log.i("2","" + c2.toString());
//                        return Double.compare(c1.getPrice(), c2.getPrice());
//                    }
//                });
//            }
//        });
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