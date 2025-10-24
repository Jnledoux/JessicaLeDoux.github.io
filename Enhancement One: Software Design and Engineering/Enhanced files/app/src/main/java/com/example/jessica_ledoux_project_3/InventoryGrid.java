package com.example.jessica_ledoux_project_3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import java.util.ArrayList;

public class InventoryGrid extends AppCompatActivity {

    private TextView userNameLabel, totalItemsLabel;
    private ImageButton addItemButton, smsButton, deleteAllButton;
    private ListView itemsListView;
    private ItemsHandler db;
    private ArrayList<Item> items;
    private CustomItemsList adapter;
    private AlertDialog alertDialog;

    public static final String UserEmail = "user_email";
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;

    private static String nameHolder, emailHolder, phoneHolder;
    private static boolean smsAuthorized = false;
    private static boolean deleteConfirmed = false;

    private ActivityResultLauncher<Intent> addItemLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_grid);

        // Initialize views
        userNameLabel = findViewById(R.id.textViewUserNameLabel);
        totalItemsLabel = findViewById(R.id.total_item_amount);
        addItemButton = findViewById(R.id.add_item_icon);
        smsButton = findViewById(R.id.sms_button);
        deleteAllButton = findViewById(R.id.delete_all);
        itemsListView = findViewById(R.id.inventory_list);
        db = new ItemsHandler(this);

        // Get user info from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameHolder = extras.getString("user_name");
            emailHolder = extras.getString("user_email");
            phoneHolder = extras.getString("user_phone");

            userNameLabel.setText(getString(R.string.Welcome, nameHolder.toUpperCase()));
        }

        loadItems();

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddItem.class);
            intent.putExtra(UserEmail, emailHolder);
            addItemLauncher.launch(intent);
        });

        SendSMS.createSmsPermissionDialog(this, new SendSMS.SmsPermissionCallback() {
            @Override
            public void onPermissionGranted() {
                InventoryGrid.AllowSendSMS();
            }

            @Override
            public void onPermissionDenied() {
                InventoryGrid.DenySendSMS();
            }
        }).show();

        deleteAllButton.setOnClickListener(v -> {
            if (items.size() > 0) {
                alertDialog = DeleteAllItems.createConfirmationDialog(this);
                alertDialog.setCancelable(true);
                alertDialog.setOnCancelListener(dialog -> handleDeleteAll());
                alertDialog.show();
            } else {
                Toast.makeText(this, "Database is Empty", Toast.LENGTH_SHORT).show();
            }
        });

        addItemLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Handle the result
                    adapter.updateItems((ArrayList<Item>) db.getAllItems());
                } else {
                    Toast.makeText(this, "Action Canceled", Toast.LENGTH_SHORT).show();
                }
            });

    }

    private void loadItems() {
        items = (ArrayList<Item>) db.getAllItems();
        totalItemsLabel.setText(String.valueOf(items.size()));

        if (items.isEmpty()) {
            Toast.makeText(this, "Database is Empty", Toast.LENGTH_SHORT).show();
        }

        adapter = new CustomItemsList(this, items, db);
        itemsListView.setAdapter(adapter);
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "SMS permission is required", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
        }
    }

    public static void YesDeleteItems() {
        deleteConfirmed = true;
    }

    public static void NoDeleteItems() {
        deleteConfirmed = false;
    }

    private void handleDeleteAll() {
        if (deleteConfirmed) {
            db.deleteAllItems();
            Toast.makeText(this, "All Items Deleted", Toast.LENGTH_SHORT).show();
            loadItems();
        }
    }

    public static void AllowSendSMS() {
        smsAuthorized = true;
    }

    public static void DenySendSMS() {
        smsAuthorized = false;
    }

    public static void SendSMSMessage(Context context) {
        if (!smsAuthorized) {
            Toast.makeText(context, "SMS alerts are disabled", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneHolder, null,
                    "Please, you have items with zero value in your Inventory App.",
                    null, null);
            Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "SMS Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
