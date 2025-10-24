package com.example.jessica_ledoux_project_3;

import androidx.appcompat.app.AlertDialog;

public class DeleteAllItems {

    public static AlertDialog createConfirmationDialog(final InventoryGrid context) {
        // Using a builder class for messages
        return new AlertDialog.Builder(context)
            .setTitle(R.string.delete_all)
            .setIcon(R.drawable.delete_all_items)
            .setCancelable(false)
            .setMessage(R.string.delete_all_msg)
            .setPositiveButton(R.string.delete_all_yes, (dialog, which) -> {
                InventoryGrid.YesDeleteItems();
                dialog.dismiss();
            })
            .setNegativeButton(R.string.delete_all_no, (dialog, which) -> {
                InventoryGrid.NoDeleteItems();
                dialog.dismiss();
            })
            .create();
    }
}

