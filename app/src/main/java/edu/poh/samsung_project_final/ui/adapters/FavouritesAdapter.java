package edu.poh.samsung_project_final.ui.adapters;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.StockEntity;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.StockDataHolder> {
    private List<StockEntity> list = new ArrayList();
    private String id;
    private Context context;
    private Activity activity;
    private ListenerFavourites listener;
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
            parseStockDataCost(id, holder, count, bought_stock);
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

    private void parseStockDataCost(String sid,StockDataHolder holder,Integer count,double bought_stock) {
        if (sid == null || sid.isEmpty()) {
            return;
        }
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setStockAndCost(response,holder,count,bought_stock);
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setStockAndCost(String response,StockDataHolder holder,Integer count, double bought_stock) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        for(int i = 0; i < data.length(); i++){
            JSONArray data_next = data.getJSONArray(i);
            if (!data_next.getString(1).equals("TQBR")){
                continue;
            }
            Double cost_d = data_next.optDouble(3);
            String cost_of_stock;
            cost_of_stock = cost_d.toString();
            holder.cost.setText(cost_of_stock + " руб");
            Double cost_final = cost_d*count;
            String all_cost;
            if(cost_final < 10.0){
                all_cost = String.format("%.3f",cost_final);
            }
            else{
                all_cost = String.format("%.2f",cost_final);
            }
            holder.all_cost.setText(all_cost + " руб");
            double percent_of_diff = ((cost_final - bought_stock)/bought_stock) * 100.0;
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
    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<StockEntity> list){
        this.list = list;
        notifyDataSetChanged();
    }
    public interface ListenerFavourites{
        void OnClickFavourites(StockEntity stockEntity);
    }
}
