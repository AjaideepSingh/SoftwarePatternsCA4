package com.example.softwarepatternsca4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import Adapters.CheckoutAdapter;
import Model.Cart;
import Model.Item;
import Model.Order;
import Model.User;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Checkout extends AppCompatActivity {
    private CheckoutAdapter checkoutAdapter;
    private RecyclerView recyclerView;
    private final ArrayList<Cart> cartArrayList = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ConstraintLayout constraintLayout;
    private TextView total;
    private final ArrayList<Order> orders = new ArrayList<>();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final Date date = new Date();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Your basket");
        recyclerView = findViewById(R.id.checkoutRCV);
        Button checkout = findViewById(R.id.checkout);
        constraintLayout = findViewById(R.id.checkoutCL);
        total = findViewById(R.id.checkoutTotal);
        showAllItemsInCart();
        checkout.setOnClickListener(v -> {
            if (cartArrayList.isEmpty()) {
                emptyCart();
            } else {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        checkout(user.getStudent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Checkout.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void checkout(String student) {
        DatabaseReference removeReference = FirebaseDatabase.getInstance().getReference("Cart");
        for (int i = 0; i < cartArrayList.size(); i++) {
            if (cartArrayList.get(i).getUserID().equals(mAuth.getUid())) {
                if(student.equalsIgnoreCase("Student Account")) {
                    Context context = new Context(new OperationDiscount());
                    cartArrayList.get(i).getItem().setPrice(context.executeStrategy(10, cartArrayList.get(i).getItem().getPrice()));
                }
                Order order = new Order(cartArrayList.get(i).getItem(), mAuth.getUid(),dateFormat.format(date));
                orders.add(order);
                removeReference.child(cartArrayList.get(i).getId()).removeValue();
                cartArrayList.clear();
                checkoutAdapter.notifyDataSetChanged();
            }
        }
        for (int i = 0; i < orders.size(); i++) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Order");
            databaseReference.push().setValue(orders.get(i));
        }
        total.setText("0 Euros");
        Toast.makeText(Checkout.this,"Order processed!",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Checkout.this, Home.class));
    }

    public void showAllItemsInCart() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalPrice = 0;
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cart = cartSnapshot.getValue(Cart.class);
                    assert cart != null;
                    cart.setId(cartSnapshot.getKey());
                    if (cart.getUserID().equals(mAuth.getUid())) {
                        totalPrice += cart.getItem().getPrice();
                        cartArrayList.add(cart);
                    }
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Checkout.this));
                checkoutAdapter = new CheckoutAdapter(cartArrayList);
                new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(checkoutAdapter);
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
                double finalTotalPrice = totalPrice;
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (user.getStudent().equals("Student Account") && !cartArrayList.isEmpty()) {
                            studentDiscount();
                            Context context = new Context(new OperationDiscount());
                            total.setText(context.executeStrategy(10, finalTotalPrice) + " Euros");
                        } else if(user.getStudent().equals("Non Student Account") && !cartArrayList.isEmpty()) {
                            total.setText(finalTotalPrice + " Euros");
                            noStudentDiscount();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Checkout.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Checkout.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Collections.swap(cartArrayList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            checkoutAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(Checkout.this, R.color.navHeader))
                    .addActionIcon(R.drawable.delete)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            DatabaseReference deleteReference = FirebaseDatabase.getInstance().getReference("Cart");
            Cart cart = cartArrayList.get(viewHolder.getAdapterPosition());
            deleteReference.child(cart.getId()).removeValue();
            cartArrayList.clear();
            checkoutAdapter.notifyDataSetChanged();
            DatabaseReference updateReference = FirebaseDatabase.getInstance().getReference("Item");
            updateReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        assert item != null;
                        item.setId(itemSnapshot.getKey());
                        if (item.getTitle().equals(cart.getItem().getTitle())) {
                            updateReference.child(item.getId()).child("stockAmount").setValue(item.getStockAmount() + cart.getItem().getStockAmount()).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Checkout.this, "Item removed from basket", Toast.LENGTH_SHORT).show();
                                    if(cartArrayList.size() == 0) {
                                        total.setText("0");
                                    }
                                } else {
                                    Toast.makeText(Checkout.this, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Checkout.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    public void noStudentDiscount() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no discount available", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void studentDiscount() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "10% student discount applied", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void emptyCart() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Your cart is empty!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Checkout.this,Home.class));
    }
}