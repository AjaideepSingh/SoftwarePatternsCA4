package com.example.softwarepatternsca4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import Adapters.CatalogueAdapter;
import Authentication.LogIn;
import Model.Cart;
import Model.Item;
import Model.User;

public class Home extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private final ArrayList<Item> items = new ArrayList<>();
    private CatalogueAdapter catalogueAdapter;
    private ImageView cartImage;
    private ConstraintLayout constraintLayout;

    @SuppressLint({"WrongConstant", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.homeToolBar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Online catalogue");
        recyclerView = findViewById(R.id.catalogueRCV);
        drawerLayout = findViewById(R.id.a);
        cartImage = findViewById(R.id.cart);
        cartImage.setOnClickListener(v -> startActivity(new Intent(Home.this,Checkout.class)));
        constraintLayout = findViewById(R.id.homeCL);
        NavigationView navigationView = findViewById(R.id.nav_view);
        getUserDetailsToPopulateHeader();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.settings:
                    startActivity(new Intent(Home.this,Settings.class));
                    break;
                case R.id.stockManager:
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            assert user != null;
                            if(user.getAccType().equalsIgnoreCase("admin")) {
                                startActivity(new Intent(Home.this,StockManager.class));
                            } else {
                                Toast.makeText(getApplicationContext(),"Admin accounts only!",Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(),"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.logOut:
                    finish();
                    startActivity(new Intent(Home.this, LogIn.class));
                    break;
            }
            drawerLayout.closeDrawer(Gravity.START);
            return true;
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cart = cartSnapshot.getValue(Cart.class);
                    if(cart.getUserID().equals(mAuth.getUid())) {
                        cartImage.setVisibility(View.VISIBLE);
                        break;
                    } else {
                        cartImage.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        getAllItems();
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
                    navUsername.setText(user.getName());
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
                for(DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    items.add(item);
                }
                if(items.isEmpty()) {
                    showInfoSnackBar();
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Home.this));
                catalogueAdapter = new CatalogueAdapter(items,Home.this);
                recyclerView.setAdapter(catalogueAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInfoSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no products for sale!", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}