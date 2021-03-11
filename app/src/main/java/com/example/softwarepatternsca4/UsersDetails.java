package com.example.softwarepatternsca4;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import java.util.ArrayList;
import Model.User;

public class UsersDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private TextView name,address,email,accountType,student;
    private Button search;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);
        spinner = findViewById(R.id.spinner3);
        name = findViewById(R.id.detailsName);
        address = findViewById(R.id.detailsAddress);
        email = findViewById(R.id.detailsEmail);
        accountType = findViewById(R.id.detailsAccountType);
        student = findViewById(R.id.detailsStudent);
        search = findViewById(R.id.usersDetailSearch);
        constraintLayout = findViewById(R.id.udcl);
        name.setVisibility(View.INVISIBLE);
        address.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        accountType.setVisibility(View.INVISIBLE);
        student.setVisibility(View.INVISIBLE);
        populateSpinner();
        search.setOnClickListener(v -> {
            if(spinner.getSelectedItemPosition() == 0) {
                showInfoSnackBar();
            } else {
                populateUI(spinner.getSelectedItem().toString());
            }
        });
    }

    public void populateSpinner() {
        ArrayList<String> userNames = new ArrayList<>();
        userNames.add("Select User!");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    userNames.add(user.getName());
                }
                ArrayAdapter<String> adapterNames = new ArrayAdapter<String>(UsersDetails.this, android.R.layout.simple_spinner_dropdown_item, userNames) {
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
                adapterNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterNames);
                spinner.setOnItemSelectedListener(UsersDetails.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersDetails.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateUI(String userName) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    if(user.getName().equalsIgnoreCase(userName)) {
                        name.setText(user.getName());
                        address.setText(user.getShippingAddress());
                        email.setText(user.getEmailAddress());
                        accountType.setText(user.getAccType());
                        student.setText(user.getStudent());
                        name.setVisibility(View.INVISIBLE);
                        address.setVisibility(View.INVISIBLE);
                        email.setVisibility(View.INVISIBLE);
                        accountType.setVisibility(View.INVISIBLE);
                        student.setVisibility(View.INVISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UsersDetails.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInfoSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Select dropdown value", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}