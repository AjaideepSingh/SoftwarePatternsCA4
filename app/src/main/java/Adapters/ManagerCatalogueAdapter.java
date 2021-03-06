package Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import Model.Item;

public class ManagerCatalogueAdapter extends RecyclerView.Adapter<ManagerCatalogueAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Item> items;
    private final Context context;

    public ManagerCatalogueAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ManagerCatalogueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull ManagerCatalogueAdapter.ViewHolder holder, final int position) {
        holder.setTitle(items.get(position).getTitle());
        holder.setCategory(items.get(position).getCategory());
        holder.setPrice(String.valueOf(items.get(position).getPrice()));
        holder.setImage(items.get(position).getImage());
        holder.setManufacturer(items.get(position).getManufacturer());
        holder.setStock(String.valueOf(items.get(position).getStockAmount()));
        holder.options.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.options);
            popup.inflate(R.menu.manageroptions);
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.updateStock) {
                    AlertDialog.Builder updateBuilder = new AlertDialog.Builder(context);
                    @SuppressLint("InflateParams")
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.stockupdater, null);
                    EditText stockAmount;
                    stockAmount = view.findViewById(R.id.updateStockEditText);
                    stockAmount.setText(String.valueOf(items.get(position).getStockAmount()));
                    updateBuilder.setPositiveButton("Update", (dialog, which) -> { });
                    updateBuilder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                    updateBuilder.setView(view);
                    AlertDialog dialog = updateBuilder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                        if (TextUtils.isEmpty(stockAmount.getText().toString())) {
                            stockAmount.setError("Field cannot be empty!");
                            stockAmount.requestFocus();
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                dialog.dismiss();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Item").child(items.get(position).getId());
                                databaseReference.child("stockAmount").setValue(Integer.parseInt(stockAmount.getText().toString()));
                                notifyDataSetChanged();
                            });
                        }
                    });
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemImage;
        private final TextView title, price, category, manufacturer, options, stock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemPicture);
            title = itemView.findViewById(R.id.itemTitle);
            price = itemView.findViewById(R.id.itemPrice);
            category = itemView.findViewById(R.id.itemCategory);
            manufacturer = itemView.findViewById(R.id.itemManufacturer);
            options = itemView.findViewById(R.id.itemOptions);
            stock = itemView.findViewById(R.id.rcvRowStock);
        }

        public void setImage(String i) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                itemImage.setImageBitmap(bitmap);
            });
        }

        public void setTitle(String t) {
            title.setText(t);
        }

        @SuppressLint("SetTextI18n")
        public void setPrice(String p) {
            price.setText(p + " Euros");
        }

        public void setCategory(String c) {
            category.setText(c);
        }

        public void setManufacturer(String m) {
            manufacturer.setText(m);
        }

        @SuppressLint("SetTextI18n")
        public void setStock(String s) {
            stock.setText(s + " Left");
        }
    }
}
