package Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.softwarepatternsca4.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import Model.Item;


public class CatalogueAdapter extends RecyclerView.Adapter<CatalogueAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Item> items;
    private final Context context;

    public CatalogueAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CatalogueAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull CatalogueAdapter.ViewHolder holder, final int position) {
        holder.setTitle(items.get(position).getTitle());
        holder.setCategory(items.get(position).getCategory());
        holder.setPrice(String.valueOf(items.get(position).getPrice()));
        holder.setImage(items.get(position).getImage());
        holder.setManufacturer(items.get(position).getManufacturer());
        holder.setStock(String.valueOf(items.get(position).getStockAmount()));
        holder.options.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.options);
            popup.inflate(R.menu.customeroptions);
            popup.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                switch (item.getItemId()) {
                    case R.id.addToCard:
                        Button add;
                        EditText quantity;
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.addtocartpopup, null);
                        add = view.findViewById(R.id.addToBasket);
                        quantity = view.findViewById(R.id.quantity);

                        //Add to cart functionality
                        //check stock allow user to select quantity
                        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        builder.setView(view);
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
        private final TextView title, price, category, manufacturer, options,stock;

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
//            Picasso.get().load(i).into(itemImage);
        }

        public void setTitle(String t) {
            title.setText("Title: " + t);
        }

        public void setPrice(String p) {
            price.setText(p + " Euros");
        }

        public void setCategory(String c) {
            category.setText("Category: " + c);
        }

        public void setManufacturer(String m) {
            manufacturer.setText("Manufacturer: " + m);
        }

        public void setStock(String s) {
            stock.setText(s + " Left");
        }
    }
}
