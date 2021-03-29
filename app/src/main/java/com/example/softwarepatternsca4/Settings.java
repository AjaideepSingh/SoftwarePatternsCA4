package com.example.softwarepatternsca4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;
import Authentication.Iterator;
import Authentication.NamesRepository;
import Model.User;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText name,cardNumber,CVV,expiryDate,address;
    private TextView email,accountType;
    private Spinner studentSpinner;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final ArrayList<String> accountTypes = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Settings");
        Button update = findViewById(R.id.settingsUpdate);
        name = findViewById(R.id.userNameSettings);
        cardNumber = findViewById(R.id.cardNumberSettings);
        CVV = findViewById(R.id.cardCVVSettings);
        expiryDate = findViewById(R.id.cardExpirySettings);
        address = findViewById(R.id.addressSettings);
        email = findViewById(R.id.settingsEmail);
        accountType = findViewById(R.id.settingsAccountType);
        studentSpinner = findViewById(R.id.studentSettingsSpinner);
        expiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strS = s.toString();
                if (start == 1 && start+count == 2 && !strS.contains("/")) {
                    expiryDate.setText(s.toString() + "/");
                } else if (start == 3 && start-before == 2 && strS.contains("/")) {
                    expiryDate.setText(s.toString().replace("/", ""));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        populateSpinner();
        fillFields();
        update.setOnClickListener(v -> {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User");
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> names = new ArrayList<>();
                    String currentUserId = null;
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        assert user != null;
                        names.add(user.getName());
                        if(Objects.equals(mAuth.getUid(), userSnapshot.getKey())) {
                            currentUserId = userSnapshot.getKey();
                        }
                    }
                    updateUserDetails(names,currentUserId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void populateSpinner() {
        accountTypes.add("Student Account");
        accountTypes.add("Non Student Account");
        ArrayAdapter<String> adapterAccountType = new ArrayAdapter<>(Settings.this,android.R.layout.simple_spinner_dropdown_item,accountTypes);
        adapterAccountType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(adapterAccountType);
        studentSpinner.setOnItemSelectedListener(Settings.this);
    }

    public void fillFields() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                name.setText(user.getName());
                cardNumber.setText(user.getCardNumber());
                CVV.setText(user.getCvv());
                expiryDate.setText(user.getExpiryDate());
                address.setText(user.getShippingAddress());
                email.setText(user.getEmailAddress());
                accountType.setText(user.getAccType());
                for (int i = 0; i < accountTypes.size(); i++) {
                    if (accountTypes.get(i).equalsIgnoreCase(user.getAccType())) {
                        studentSpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Settings.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateUserDetails(ArrayList<String> names, String userID) {
        NamesRepository namesRepository = new NamesRepository();
        ArrayList<String> namesInDB = new ArrayList<>();
        for (Iterator iterator = namesRepository.getIterator(names); iterator.hasNext(); ) {
            namesInDB.add((String) iterator.next());
        }
        if(TextUtils.isEmpty(name.getText().toString())) {
            name.setError("Error Field cannot be empty!");
            name.requestFocus();
        } else if (namesInDB.contains(name.getText().toString()) && !Objects.requireNonNull(mAuth.getUid()).equalsIgnoreCase(userID)) {
            name.setError("User name already exists");
            name.requestFocus();
        } else if(TextUtils.isEmpty(address.getText().toString())) {
            address.setError("Error Field cannot be empty!");
            address.requestFocus();
        } else if(TextUtils.isEmpty(cardNumber.getText().toString())) {
            cardNumber.setError("Error Field cannot be empty!");
            cardNumber.requestFocus();
        } else if(TextUtils.isEmpty(expiryDate.getText().toString())) {
            expiryDate.setError("Error Field cannot be empty!");
            expiryDate.requestFocus();
        } else if(TextUtils.isEmpty(CVV.getText().toString())) {
            CVV.setError("Error Field cannot be empty!");
            CVV.requestFocus();
        } else {
            DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
            updateRef.child("name").setValue(name.getText().toString().trim());
            updateRef.child("shippingAddress").setValue(address.getText().toString().trim());
            updateRef.child("cardNumber").setValue(cardNumber.getText().toString().trim());
            updateRef.child("expiryDate").setValue(expiryDate.getText().toString().trim());
            updateRef.child("cvv").setValue(CVV.getText().toString().trim());
            updateRef.child("student").setValue(studentSpinner.getSelectedItem().toString());
            updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    name.setText(user.getName());
                    address.setText(user.getShippingAddress());
                    cardNumber.setText(user.getCardNumber());
                    expiryDate.setText(user.getExpiryDate());
                    CVV.setText(user.getCvv());
                    for(int i = 0; i < accountTypes.size(); i++){
                        if(accountTypes.get(i).equalsIgnoreCase(user.getAccType())) {
                            studentSpinner.setSelection(i);
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Settings.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Settings.this,Home.class));
    }
}