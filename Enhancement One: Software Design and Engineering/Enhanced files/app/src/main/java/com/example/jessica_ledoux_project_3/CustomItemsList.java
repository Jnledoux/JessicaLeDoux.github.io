package com.example.jessica_ledoux_project_3;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


import java.util.ArrayList;

public class CustomItemsList extends BaseAdapter {
    private final Activity context;
    private final ItemsHandler db;
    private ArrayList<Item> items;
    private PopupWindow popupWindow;

    public CustomItemsList(Activity context, ArrayList<Item> items, ItemsHandler db) {
        this.context = context;
        this.items = items;
        this.db = db;
    }

    static class ViewHolder {
        TextView itemDescription;
        TextView itemQuantity;
        TextView itemCategory;
        ImageButton editButton;
        ImageButton deleteButton;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.custom_grid_rows, parent, false);
        }

        Item item = getItem(position);

        // Set letter icon
        ImageButton icon = view.findViewById(R.id.grid_image);
        Bitmap letterIcon = LetterIconGenerator.create(context, item.getDescription(), 48);
        icon.setImageBitmap(letterIcon);

        // Set other fields
        TextView desc = view.findViewById(R.id.custom_item_desc);
        desc.setText(item.getDescription());

        TextView qty = view.findViewById(R.id.custom_item_qty);
        qty.setText(String.valueOf(item.getQuantity()));

        TextView category = view.findViewById(R.id.custom_item_category);
        category.setText(item.getCategory());

        ImageButton deleteBtn = view.findViewById(R.id.custom_delete);
        deleteBtn.setOnClickListener(v -> {
            db.deleteItem(item);
            updateItems(new ArrayList<>(db.getAllItems()));
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // Allows for updates but keeps the items list within other parts private
    public void updateItems(ArrayList<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    // pop up menu to edit item
    private void showEditPopup(int position) {
        LayoutInflater inflater = context.getLayoutInflater();
        View layout = inflater.inflate(R.layout.edit_item_popup, context.findViewById(R.id.edit_popup_element));

        popupWindow = new PopupWindow(layout,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        EditText editDesc = layout.findViewById(R.id.edit_item_desc);
        EditText editQty = layout.findViewById(R.id.edit_item_qty);
        EditText editCategory = layout.findViewById(R.id.edit_item_cat);

        Item item = getItem(position);
        editDesc.setText(item.getDescription());
        editQty.setText(item.getQuantity());
        editCategory.setText(item.getCategory());

        Button saveButton = layout.findViewById(R.id.editSaveButton);
        Button cancelButton = layout.findViewById(R.id.edit_cancel_button);

        // adding on click lister to save updates
        saveButton.setOnClickListener(v -> {
            item.setDescription(editDesc.getText().toString().trim());
            item.setQuantity(editQty.getText().toString().trim());
            item.setCategory(editCategory.getText().toString().trim());

            db.updateItem(item);
            items.set(position, item);
            notifyDataSetChanged();

            Toast.makeText(context, "Item Updated", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            Toast.makeText(context, "Action Canceled", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
        });
    }
}

