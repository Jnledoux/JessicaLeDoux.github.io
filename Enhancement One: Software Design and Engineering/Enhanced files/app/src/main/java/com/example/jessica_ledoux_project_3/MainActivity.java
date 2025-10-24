package com.example.jessica_ledoux_project_3;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton, newUserButton, forgotButton;
    private String nameHolder, phoneHolder, emailHolder, passwordHolder;
    private boolean isInputEmpty;
    private PopupWindow popupWindow;
    private SQLiteDatabase userDB;
    private UsersHandler dbHandler;
    private String tempPassword = "NOT_FOUND";
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // Initiate variables, buttons, and databases
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        forgotButton = findViewById(R.id.forgot_button);
        newUserButton = findViewById(R.id.newUser_button);
        dbHandler = new UsersHandler(this);

        // Adding click listener for login function
        loginButton.setOnClickListener(v -> login());

        // Adding click listener to register a new user
        newUserButton.setOnClickListener(v -> startActivity(new Intent(this, NewUserReg.class)));

        // Adding click listener for forgotten password popup
        forgotButton.setOnClickListener(v -> {
            emailHolder = emailInput.getText().toString().trim();
            if (!emailHolder.isEmpty()) {
                showForgotPasswordPopup();
            } else {
                Toast.makeText(this, "User Email is Empty", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Login function
    private void login() {
        String message = validateInputs();

        if (!isInputEmpty) {
            // Opening the user database
            userDB = dbHandler.getReadableDatabase();

            // Searching for email
            Cursor cursor = userDB.query(UsersHandler.TABLE_NAME, null, UsersHandler.COL_EMAIL + "=?", new String[]{emailHolder}, null, null, null);

            if (cursor.moveToFirst()) {
                tempPassword = cursor.getString(cursor.getColumnIndexOrThrow(UsersHandler.COL_PASSWORD));
                nameHolder = cursor.getString(cursor.getColumnIndexOrThrow(UsersHandler.COL_NAME));
                phoneHolder = cursor.getString(cursor.getColumnIndexOrThrow(UsersHandler.COL_PHONE));
                cursor.close();
            }

            dbHandler.close();
            checkLoginResult();
        } else {
            // Message if anything is left empty
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private String validateInputs() {
        // Holder for variables
        emailHolder = emailInput.getText().toString().trim();
        passwordHolder = passwordInput.getText().toString().trim();

        if (emailHolder.isEmpty()) {
            emailInput.requestFocus();
            isInputEmpty = true;
            return "User Email is Empty";
        } else if (passwordHolder.isEmpty()) {
            passwordInput.requestFocus();
            isInputEmpty = true;
            return "User Password is Empty";
        }

        isInputEmpty = false;
        return "";
    }

    private void checkLoginResult() {
        if (tempPassword.equalsIgnoreCase(passwordHolder)) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, InventoryGrid.class);
            intent.putExtra("user_name", nameHolder);
            intent.putExtra("user_email", emailHolder);
            intent.putExtra("user_phone", phoneHolder);
            startActivity(intent);

            clearInputs();
        } else {
            Toast.makeText(this, "Incorrect Email or Password\nor User Not Registered", Toast.LENGTH_LONG).show();
        }

        tempPassword = "NOT_FOUND";
    }

    // Clear and empty text for the two inputs
    private void clearInputs() {
        emailInput.getText().clear();
        passwordInput.getText().clear();
    }

    private void showForgotPasswordPopup() {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.forgot_password, activity.findViewById(R.id.forgot_popup_element));

        popupWindow = new PopupWindow(layout, 800, 800, true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        // Opening the user database
        userDB = dbHandler.getReadableDatabase();

        // Adding email search
        Cursor cursor = userDB.query(UsersHandler.TABLE_NAME, null, UsersHandler.COL_EMAIL + "=?", new String[]{emailHolder}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            phoneHolder = cursor.getString(cursor.getColumnIndexOrThrow(UsersHandler.COL_PHONE));
            tempPassword = cursor.getString(cursor.getColumnIndexOrThrow(UsersHandler.COL_PASSWORD));
            cursor.close();
        }

        dbHandler.close();

        Button sendMsg = layout.findViewById(R.id.forgot_send_msg);
        Button cancel = layout.findViewById(R.id.forgot_cancel_button);
        TextView passwordDisplay = layout.findViewById(R.id.forgot_password_display);

        passwordDisplay.setText("Password: " + tempPassword);

        sendMsg.setOnClickListener(v -> {
            Toast.makeText(activity, "Password sent to your phone", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        cancel.setOnClickListener(v -> {
            Toast.makeText(activity, "Action Canceled", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }
}
