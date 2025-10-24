package com.example.jessica_ledoux_project_3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicReference;

public class AddItem extends AppCompatActivity {

    String Email_Holder, Desc_Holder, Qty_Holder, Category_Holder;
    TextView UserEmail;
    ImageButton Increase_Qty, Decrease_Qty;
    EditText ItemDescValue, ItemQtyValue, ItemCategoryValue;
    Button Cancel_Button, Add_Item_Button;
    Boolean EmptyHolder;
    ItemsHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        // Initiate variables, buttons, and databases
        UserEmail = findViewById(R.id.current_logged_user);
        ItemDescValue = findViewById(R.id.item_desc);
        ItemCategoryValue = findViewById(R.id.item_category);
        Increase_Qty = findViewById(R.id.item_qty_inc);
        Decrease_Qty = findViewById(R.id.item_qty_dec);
        ItemQtyValue = findViewById(R.id.item_quantity);
        Cancel_Button = findViewById(R.id.add_cancel_button);
        Add_Item_Button = findViewById(R.id.add_item_button);
        db = new ItemsHandler(this);

        // Set default quantity to 0
        ItemQtyValue.setText("0");

        // Receiving user email sent by login
        Intent intent = getIntent();
        Email_Holder = intent.getStringExtra(InventoryGrid.UserEmail);
        UserEmail.setText(getString(R.string.logged_user, Email_Holder));

        // Increase quantity
        Increase_Qty.setOnClickListener(view -> {
            int input = 0;
            String value = ItemQtyValue.getText().toString().trim();
            if (!value.isEmpty() && value.matches("\\d+")) {
                input = Integer.parseInt(value);
            }
            ItemQtyValue.setText(String.valueOf(input + 1));
        });

        // Decrease quantity
        Decrease_Qty.setOnClickListener(view -> {
            String qty = ItemQtyValue.getText().toString().trim();
            if (qty.isEmpty() || !qty.matches("\\d+")) {
                Toast.makeText(this, "Invalid Quantity", Toast.LENGTH_LONG).show();
                return;
            }

            int input = Integer.parseInt(qty);
            if (input == 0) {
                Toast.makeText(this, "Item Quantity is Zero", Toast.LENGTH_LONG).show();
            } else {
                ItemQtyValue.setText(String.valueOf(input - 1));
            }
        });

        // Cancel button
        Cancel_Button.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // Add item button
        Add_Item_Button.setOnClickListener(view -> InsertItemIntoDatabase());
    }

    // Insert item into database
    public void InsertItemIntoDatabase() {
        String message = CheckEditTextNotEmpty();

        if (!EmptyHolder) {
            String email = Email_Holder;
            String desc = Desc_Holder;
            String qty = Qty_Holder;
            String unit = Category_Holder;

            Item item = new Item(email, desc, qty, unit);

            // Message to confirm successful or failure of addition
            try {
                db.createItem(item);
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            // Message if the description is empty
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    // Checking to ensure item description is not empty
    public String CheckEditTextNotEmpty() {
        // Getting and storing values into holders
        Desc_Holder = ItemDescValue.getText().toString().trim();
        Category_Holder = ItemCategoryValue.getText().toString().trim();
        Qty_Holder = ItemQtyValue.getText().toString().trim();
        String message = "";

        if (Desc_Holder.isEmpty()) {
            ItemDescValue.requestFocus();
            EmptyHolder = true;
            message = "Item Name is Empty";
        } else if (Category_Holder.isEmpty()) {
            ItemCategoryValue.requestFocus();
            EmptyHolder = true;
            message = "Item Unit is Empty";
        } else if (Qty_Holder.isEmpty() || !Qty_Holder.matches("\\d+")) {
            ItemQtyValue.requestFocus();
            EmptyHolder = true;
            message = "Item Quantity is invalid";
        } else {
            EmptyHolder = false;
        }

        return message;
    }
}
