package edu.poh.samsung_project_final.ui.ui.adapter;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.StockDataHolder> {
    private List<StockEntity> list = new ArrayList();
    private String id;
    private Context context;
    private ListenerFavourites listener;
    private stockSearchViewModel model;
    private double balanse;
    private static final int VIEW_TYPE_FIRST_ITEM = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public FavouritesAdapter(ListenerFavourites listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public StockDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_FIRST_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_for_balance_in_rub, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_of_favourites, parent, false);
        }
        return new StockDataHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged", "DefaultLocale"})
    @Override
    public void onBindViewHolder(StockDataHolder holder, int position) {
        if(position == 0){
            holder.balance.setText(String.format("%.2f",balanse) + " руб");
        }
        else {
            StockEntity stockEntity = this.list.get(position - 1);
            id = stockEntity.id_of_stock;
            String name_of_stock = stockEntity.name_of_stock;
            Integer count = stockEntity.quantity_of_stock_ent;
            double bought_stock = stockEntity.stock_price_when_bought;
            model.setAllForStockPage(id, context, new DataLoadCallback() {
                @Override
                public void onDataLoaded() {
                    for (parseStockInfoModel parse: model.stock_page_list){
                        Double cost = Double.parseDouble(parse.stock_page_cost_of_stock);
                        holder.cost.setText(parse.stock_page_cost_of_stock);
                        holder.all_cost.setText(cost * count + " руб");
                        double percent_of_diff = ((cost*count - bought_stock)/bought_stock) * 100.0;
                        if (percent_of_diff < 0.0){
                            holder.percent.setTextColor(Color.parseColor("#D41307"));
                            String persent_str = String.format("%.2f",abs(percent_of_diff));
                            holder.percent.setText("- " + persent_str + " %");
                        }
                        else{
                            holder.percent.setTextColor(Color.parseColor("#07D434"));
                            String persent_str = String.format("%.2f",percent_of_diff);
                            holder.percent.setText("+ " + persent_str + " %");
                        }
                    }
                }
            });
            holder.quantity.setText(count + " шт.");
            if (name_of_stock.length() > 30) {
                holder.name.setText(name_of_stock.substring(0, 30) + "...");
            } else {
                holder.name.setText(name_of_stock);
            }
            holder.recycler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.OnClickFavourites(stockEntity);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return this.list.size() + 1;
    }

    public void setViewModel(stockSearchViewModel model){
        this.model = model;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FIRST_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public void setContext (Context context){
        this.context = context;
    }

    public void setBalance(double balance){
        this.balanse = balance;
    }

    public class StockDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView all_cost;
        private final TextView cost;
        private final CardView recycler;
        private final TextView percent;
        private final TextView quantity;
        private final TextView balance;
        public StockDataHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.which_stock_is_here_favourites);
            all_cost = (TextView) itemView.findViewById(R.id.all_cost_of_stock_which_is_here_favourites);
            recycler = (CardView) itemView.findViewById(R.id.cardViewRecyclerFavourites);
            cost = (TextView) itemView.findViewById(R.id.cost_of_stock_which_is_here_favourites);
            percent = (TextView) itemView.findViewById(R.id.precent_of_stock_which_is_here_favourites);
            quantity = (TextView) itemView.findViewById(R.id.quantity_of_stock_which_is_here_favourites);
            balance = (TextView) itemView.findViewById(R.id.balance_of_user);
        }

        @Override
        public void onClick(View v) {
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<StockEntity> list){
        this.list = list;
        notifyDataSetChanged();
    }
    public interface ListenerFavourites{
        void OnClickFavourites(StockEntity stockEntity);
    }
}
