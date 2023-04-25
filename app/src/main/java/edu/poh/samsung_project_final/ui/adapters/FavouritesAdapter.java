package edu.poh.samsung_project_final.ui.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import edu.poh.samsung_project_final.data.repositories.UserRepository;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.StockDataHolder> {
    private AlertDialog.Builder builder;
    private UserRepository userRepository;
    private List<StockEntity> list = new ArrayList();
    private String id;
    //private ListItemOfFavouritesBinding binding;
    private Context context;

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
        Log.d("MyLon",id);
        parseStockDataCost(id,holder,count);
        if (name_of_stock.length() > 30){
            holder.name.setText(name_of_stock.substring(0,30)+"...");
        }else{
            holder.name.setText(name_of_stock);
        }
        holder.delete.setOnClickListener(view -> {
            builder.setMessage("Вы уверены, что хотите удалить эту ценную бумагу из избранных").setCancelable(false).setPositiveButton("Нет", (dialog, id) -> dialog.cancel()).setNegativeButton("Да", (dialog, id) -> {
                list.remove(stockEntity);
                userRepository.deleteStock(stockEntity);
                notifyDataSetChanged();
                dialog.dismiss();
            });
            builder.create().show();
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
        private final TextView cost;
        private final ImageView delete;
        public StockDataHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.which_stock_is_here_favourites);
            cost = (TextView) itemView.findViewById(R.id.cost_of_stock_which_is_here_favourites);
            delete = (ImageView) itemView.findViewById(R.id.deleteFromFavourites);
        }

        @Override
        public void onClick(View v) {
        }
    }

    private void parseStockDataCost(String sid,StockDataHolder holder,Integer count) {
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
                            setStockAndCost(response,holder,count);
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
    private void setStockAndCost(String response,StockDataHolder holder,Integer count) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        JSONArray data_next = data.getJSONArray(0);
        double cost_d = data_next.optDouble(3)*count;
        String cost;
        if(cost_d < 10.0){
            cost = String.format("%.3f",cost_d);
        }
        else if (cost_d < 100.0){
            cost = String.format("%.2f",cost_d);
        }
        else{
            cost = String.format("%.1f",cost_d);
        }
        holder.cost.setText(cost+" руб");
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<StockEntity> list){
        Log.d("MyLon",list.toString());
        this.list = list;
        notifyDataSetChanged();
    }

}
