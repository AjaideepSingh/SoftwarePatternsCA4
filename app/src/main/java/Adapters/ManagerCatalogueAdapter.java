package Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
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
        holder.options.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.options);
            popup.inflate(R.menu.manageroptions);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.removeItem:
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to delete this item from your favourites");
                                builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
                                builder.setPositiveButton("Yes", (dialog, which) -> {
                                    Item itemToDelete = new Item();
                                    for(int i = 0; i < items.size(); i++) {
                                        if(items.get(i).getTitle().equalsIgnoreCase(items.get(position).getTitle())) {
                                            itemToDelete = items.get(i);
                                            break;
                                        }
                                    }
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Item");
                                    Item finalItemToDelete = itemToDelete;
                                    databaseReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                                Item item = itemSnapshot.getValue(Item.class);
                                                assert item != null;
                                                item.setId(itemSnapshot.getKey());
                                                if(item.getTitle().equalsIgnoreCase(finalItemToDelete.getTitle())) {
                                                    databaseReference.child(item.getId()).removeValue().addOnCompleteListener(task -> {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(context,"Item removed",Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(context,"Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    items.clear();
                                    notifyDataSetChanged();
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                        break;
                    case R.id.updateStock:
                        break;
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
        private final TextView title, price, category, manufacturer, options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemPicture);
            title = itemView.findViewById(R.id.itemTitle);
            price = itemView.findViewById(R.id.itemPrice);
            category = itemView.findViewById(R.id.itemCategory);
            manufacturer = itemView.findViewById(R.id.itemManufacturer);
            options = itemView.findViewById(R.id.itemOptions);
        }

        public void setImage(String i) {
//            Picasso.get().load(i).into(itemImage);
        }

        public void setTitle(String t) {
            title.setText(t);
        }

        public void setPrice(String p) {
            price.setText(p);
        }

        public void setCategory(String c) {
            category.setText(c);
        }

        public void setManufacturer(String m) {
            manufacturer.setText(m);
        }
    }
}
