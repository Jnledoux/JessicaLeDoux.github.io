package com.example.jessica_ledoux_project_3;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewUserReg extends AppCompatActivity {

    private Button registerButton, cancelButton;
    private EditText nameInput, phoneInput, emailInput, passwordInput;
    private boolean isInputEmpty;
    private SQLiteDatabase userDB;
    private UsersHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initiate variables, buttons, and databases
        nameInput = findViewById(R.id.user_name);
        phoneInput = findViewById(R.id.user_phoneNum);
        emailInput = findViewById(R.id.user_email);
        passwordInput = findViewById(R.id.user_password);
        registerButton = findViewById(R.id.sign_up_button);
        cancelButton = findViewById(R.id.cancel_button);
        dbHandler = new UsersHandler(this);

        // Adding click listener for registration button
        registerButton.setOnClickListener(v -> {
            String message = validateInputs();
            if (!isInputEmpty) {
                checkEmailAndRegister();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });

        // Adding click listener to cancel
        cancelButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    // Check if inputs are empty
    private String validateInputs() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pass = passwordInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.requestFocus();
            isInputEmpty = true;
            return "User Name is Empty";
        } else if (phone.isEmpty()) {
            phoneInput.requestFocus();
            isInputEmpty = true;
            return "User Phone is Empty";
        } else if (email.isEmpty()) {
            emailInput.requestFocus();
            isInputEmpty = true;
            return "User Email is Empty";
        } else if (pass.isEmpty()) {
            passwordInput.requestFocus();
            isInputEmpty = true;
            return "User Password is Empty";
        }

        isInputEmpty = false;
        return "";
    }

    // Check if email already exists
    private void checkEmailAndRegister() {
        String email = emailInput.getText().toString().trim();
        userDB = dbHandler.getReadableDatabase();

        Cursor cursor = userDB.query(UsersHandler.TABLE_NAME, null,
                UsersHandler.COL_EMAIL + "=?", new String[]{email},
                null, null, null);

        boolean emailExists = cursor.moveToFirst();
        cursor.close();
        dbHandler.close();

        if (emailExists) {
            Toast.makeText(this, "Email Already Exists", Toast.LENGTH_LONG).show();
        } else {
            insertUserIntoDatabase();
        }
    }

    // Register new user into database
    private void insertUserIntoDatabase() {
        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String pass = passwordInput.getText().toString().trim();

        User user = new User(name, phone, email, pass);
        dbHandler.createUser(user);

        Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_LONG).show();
        clearInputs();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // Clear and empty holders
    private void clearInputs() {
        nameInput.getText().clear();
        phoneInput.getText().clear();
        emailInput.getText().clear();
        passwordInput.getText().clear();
    }
}

