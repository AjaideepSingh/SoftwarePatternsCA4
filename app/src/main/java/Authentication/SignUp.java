package Authentication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.softwarepatternsca4.Home;
import com.example.softwarepatternsca4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;
import Model.User;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText emailAddress, userName, password, address, cardNo, cvv, expiryDate;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Spinner accountType, discount;

    @SuppressLint("StaticFieldLeak")
    private static SignUp instance;

    public static SignUp getInstance() {
        if (instance == null) {
            instance = new SignUp();
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up Form");
        userName = findViewById(R.id.userName);
        emailAddress = findViewById(R.id.emailAddressSignUp);
        password = findViewById(R.id.signUpPasswordTextField);
        address = findViewById(R.id.address);
        cardNo = findViewById(R.id.cardNumber);
        cvv = findViewById(R.id.cvv);
        expiryDate = findViewById(R.id.expiry);
        TextView signInInstead = findViewById(R.id.existing_account);
        Button registerButton = findViewById(R.id.button4);
        accountType = findViewById(R.id.spinner2);
        discount = findViewById(R.id.spinner);
        populateSpinner();
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
        signInInstead.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LogIn.class));
        });
        registerButton.setOnClickListener(v -> {
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User");
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> names = new ArrayList<>();
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        assert user != null;
                        names.add(user.getName());
                    }
                    createUser(names);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void populateSpinner() {
        ArrayList<String> accTypes = new ArrayList<>();
        accTypes.add("Account type?");
        accTypes.add("Customer");
        accTypes.add("Admin");
        ArrayAdapter<String> adapterGender = new ArrayAdapter<String>(SignUp.this, android.R.layout.simple_spinner_dropdown_item, accTypes) {
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
        adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountType.setAdapter(adapterGender);
        accountType.setOnItemSelectedListener(SignUp.this);
        ArrayList<String> studentAccount = new ArrayList<>();
        studentAccount.add("Are you a student?");
        studentAccount.add("Student Account");
        studentAccount.add("Non Student Account");
        ArrayAdapter<String> adapterStudent = new ArrayAdapter<String>(SignUp.this, android.R.layout.simple_spinner_dropdown_item, studentAccount) {
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
        discount.setAdapter(adapterStudent);
        discount.setOnItemSelectedListener(SignUp.this);
    }

    public void createUser(ArrayList<String> names) {
        NamesRepository namesRepository = new NamesRepository();
        ArrayList<String> namesInDB = new ArrayList<>();
        for (Iterator iterator = namesRepository.getIterator(names); iterator.hasNext(); ) {
            namesInDB.add(String.valueOf(iterator.next()).toLowerCase());
        }
        if (TextUtils.isEmpty(userName.getText().toString().trim().toLowerCase())) {
            userName.setError("Name is required!");
            userName.requestFocus();
        } else if (namesInDB.contains(userName.getText().toString())) {
            userName.setError("User name already exists");
            userName.requestFocus();
        } else if (TextUtils.isEmpty(emailAddress.getText().toString().trim())) {
            emailAddress.setError("Email is required!");
            emailAddress.requestFocus();
        } else if (TextUtils.isEmpty(address.getText().toString())) {
            address.setError("Address is required!");
            address.requestFocus();
        } else if (TextUtils.isEmpty(password.getText().toString()) || password.getText().toString().length() < 6) {
            password.setError("6 digit password!");
            password.requestFocus();
        } else if (TextUtils.isEmpty(cardNo.getText().toString()) || cardNo.getText().toString().length() != 16) {
            cardNo.setError("16 digits only");
            cardNo.requestFocus();
        } else if (TextUtils.isEmpty(cvv.getText().toString()) || cvv.getText().toString().length() != 3) {
            cvv.setError("3 digit only");
            cvv.requestFocus();
        } else if (TextUtils.isEmpty(expiryDate.getText().toString())) {
            expiryDate.setError("Expiry dare is required!");
            expiryDate.requestFocus();
        } else if(accountType.getSelectedItemPosition() == 0 || discount.getSelectedItemPosition() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SignUp.this);
                dlgAlert.setMessage("Spinner values must be selected!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
        } else {
            mAuth.createUserWithEmailAndPassword(emailAddress.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User.Builder()
                            .setName(userName.getText().toString())
                            .setShippingAddress(address.getText().toString().trim())
                            .setEmailAddress(emailAddress.getText().toString().trim())
                            .setCardNumber(cardNo.getText().toString().trim())
                            .setCvv(cvv.getText().toString())
                            .setExpiryDate(expiryDate.getText().toString())
                            .setAccType(accountType.getSelectedItem().toString())
                            .setStudent(discount.getSelectedItem().toString()).create();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("User").child(Objects.requireNonNull(mAuth.getUid())).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Account created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            userName.setText("");
                            emailAddress.setText("");
                            password.setText("");
                            address.setText("");
                            cardNo.setText("");
                            cvv.setText("");
                            expiryDate.setText("");
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Error Occurred!" + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
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
}


