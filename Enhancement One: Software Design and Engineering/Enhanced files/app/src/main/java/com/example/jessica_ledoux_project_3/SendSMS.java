package com.example.jessica_ledoux_project_3;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SendSMS extends AppCompatActivity {

    public interface SmsPermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    public static AlertDialog createSmsPermissionDialog(InventoryGrid context, SmsPermissionCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(R.string.sms_title)
                .setIcon(R.drawable.sms_message)
                .setCancelable(false)
                .setMessage(R.string.sms_msg)
                .setPositiveButton(R.string.enable_button, (dialog, which) -> {
                    Toast.makeText(context, "SMS Alerts Enabled", Toast.LENGTH_LONG).show();
                    callback.onPermissionGranted();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.disable_button, (dialog, which) -> {
                    Toast.makeText(context, "SMS Alerts Disabled", Toast.LENGTH_LONG).show();
                    callback.onPermissionDenied();
                    dialog.dismiss();
                });

        return builder.create();
    }
}
