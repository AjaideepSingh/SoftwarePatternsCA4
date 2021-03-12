package Authentication;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.softwarepatternsca4.Home;
import com.example.softwarepatternsca4.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import java.util.ArrayList;
import java.util.Objects;
import Model.User;

public class SignUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText emailAddress, userName, password,address,cardNo,cvv,expiryDate;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Spinner accountType,discount;
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
        signInInstead.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LogIn.class));
        });
        registerButton.setOnClickListener(v -> {
            createUser();
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

    public void createUser() {
        if(accountType.getSelectedItemPosition() == 0 || discount.getSelectedItemPosition() == 0) {
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SignUp.this);
            dlgAlert.setMessage("Spinner values must be selected!");
            dlgAlert.setTitle("Error...");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        } else if (TextUtils.isEmpty(userName.getText().toString().trim())) {
            userName.setError("User Name is required!");
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
        }  else {
            mAuth.createUserWithEmailAndPassword(emailAddress.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(userName.getText().toString().trim(),address.getText().toString().trim(),emailAddress.getText().toString().trim(),cardNo.getText().toString(),cvv.getText().toString(),expiryDate.getText().toString(),accountType.getSelectedItem().toString(),discount.getSelectedItem().toString());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("User").child(Objects.requireNonNull(mAuth.getUid())).setValue(user).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                        notificationManager.createNotificationChannel(notificationChannel);
                                    }
                                    String message = "Thank you for creating an account with us!";
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(SignUp.this, "My Notification").setSmallIcon(
                                            R.drawable.info).setContentTitle("Welcome").setContentText(message).setAutoCancel(true);
                                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(SignUp.this);
                                    notificationManagerCompat.notify(0, builder.build());
                                    Toast.makeText(getApplicationContext(), "User Saved Successfully!", Toast.LENGTH_SHORT).show();
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


