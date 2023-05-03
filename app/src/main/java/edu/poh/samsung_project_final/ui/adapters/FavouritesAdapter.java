package edu.poh.samsung_project_final.ui.adapters;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
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
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.data.repositories.UserRepository;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.StockDataHolder> {
    private AlertDialog.Builder builder;
    private UserRepository userRepository;
    private List<StockEntity> list = new ArrayList();
    private String id;
    private NavController navController;
    private NavHostFragment navHostFragment;
    //private ListItemOfFavouritesBinding binding;
    private Context context;
    private ListenerFavourites listener;

    public FavouritesAdapter(ListenerFavourites listener){
        this.listener = listener;
    }
    @NonNull
    @Override
    public StockDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_of_favourites,parent,false);
        builder = new AlertDialog.Builder(parent.getContext());
        userRepository = new UserRepository((Application) parent.getContext().getApplicationContext());
        return new StockDataHolder(itemView);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(StockDataHolder holder, int position) {
        StockEntity stockEntity = this.list.get(position);
        id = stockEntity.id_of_stock;
        String name_of_stock = stockEntity.name_of_stock;
        Integer count = stockEntity.quantity_of_stock_ent;
        double bought_stock = stockEntity.stock_price_when_bought;
        Log.d("MyLon",id);
        parseStockDataCost(id,holder,count,bought_stock);
        holder.quantity.setText(count+" шт.");
        if (name_of_stock.length() > 30){
            holder.name.setText(name_of_stock.substring(0,30)+"...");
        }else{
            holder.name.setText(name_of_stock);
        }
//        holder.delete.setOnClickListener(view -> {
//            builder.setMessage("Вы уверены, что хотите удалить эту ценную бумагу из избранных").setCancelable(false).setPositiveButton("Нет", (dialog, id) -> dialog.cancel()).setNegativeButton("Да", (dialog, id) -> {
//                list.remove(stockEntity);
//                userRepository.deleteStock(stockEntity);
//                notifyDataSetChanged();
//                dialog.dismiss();
//            });
//            builder.create().show();
//        });
        holder.recycler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClickFavourites(stockEntity);
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void setContext (Context context){
        this.context = context;
    }

    public class StockDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView all_cost;
        private final TextView cost;
        //private final ImageView delete;
        private final CardView recycler;
        private final TextView percent;
        private final TextView quantity;
        public StockDataHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.which_stock_is_here_favourites);
            all_cost = (TextView) itemView.findViewById(R.id.all_cost_of_stock_which_is_here_favourites);
            //delete = (ImageView) itemView.findViewById(R.id.deleteFromFavourites);
            recycler = (CardView) itemView.findViewById(R.id.cardViewRecyclerFavourites);
            cost = (TextView) itemView.findViewById(R.id.cost_of_stock_which_is_here_favourites);
            percent = (TextView) itemView.findViewById(R.id.precent_of_stock_which_is_here_favourites);
            quantity = (TextView) itemView.findViewById(R.id.quantity_of_stock_which_is_here_favourites);
        }

        @Override
        public void onClick(View v) {
        }
    }

    private void parseStockDataCost(String sid,StockDataHolder holder,Integer count,double bought_stock) {
        if (sid == null || sid.isEmpty()) {
            return;
        }
        final String[] cost_of_stock = {""};
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("MyLon","response");
                            setStockAndCost(response,holder,count,bought_stock);
                            Log.d("MyLon",cost_of_stock[0]);
                        } catch (JSONException e) {
                            Log.d("MyLon","Errorrrrrr");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyLon", "VolleyError:" + error.toString());
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
            if(cost_d < 10.0){
                cost_of_stock = String.format("%.3f",cost_d);
            }
            else if (cost_d < 100.0){
                cost_of_stock = String.format("%.2f",cost_d);
            }
            else{
                cost_of_stock = String.format("%.1f",cost_d);
            }
            holder.cost.setText(cost_of_stock + " руб");
            Double cost_final = cost_d*count;
            String all_cost;
            if(cost_final < 10.0){
                all_cost = String.format("%.3f",cost_final);
            }
            else if (cost_final < 1000.0){
                all_cost = String.format("%.2f",cost_final);
            }
            else{
                all_cost = String.format("%.1f",cost_final);
            }
            holder.all_cost.setText(all_cost + " руб");
            double percent_of_diff = ((cost_final - bought_stock)/bought_stock) * 100.0;
            if (percent_of_diff < 0){
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
        Log.d("MyLon",list.toString());
        this.list = list;
        notifyDataSetChanged();
    }
    public interface ListenerFavourites{
        void OnClickFavourites(StockEntity stockEntity);
    }
}
