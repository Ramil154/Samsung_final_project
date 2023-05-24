package edu.poh.samsung_project_final.ui.data.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

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

import edu.poh.samsung_project_final.ui.data.OnDataLoadedListener;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;

public class parseStockInfoModel{
    private ArrayList<parseStockInfoModel> list = new ArrayList<>();
    public String name_of_stock;
    private OnDataLoadedListener listener;
    public String cost_of_stock;
    public String id_of_stock;

    public parseStockInfoModel(){}
    public parseStockInfoModel(String name_of_stock,String cost_of_stock,String id_of_stock){
        this.cost_of_stock = cost_of_stock;
        this.name_of_stock = name_of_stock;
        this.id_of_stock = id_of_stock;
    }
    public interface OnDataLoadedListener {
        void onDataLoaded(ArrayList<parseStockInfoModel> data);
    }

    public void setListener(OnDataLoadedListener listener) {
        this.listener = listener;
    }
    private void requestStockData(Context context){
        final ArrayList<parseStockInfoModel>[] region_list = new ArrayList[]{new ArrayList<>()};
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities.json";
        RequestQueue queque = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            region_list[0] = parseStockData(response);
                            if(listener != null){
                                listener.onDataLoaded(region_list[0]);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyWay", "VolleyError:" + error.toString());
            }
        });
        queque.add(stringRequest);
    }

    public ArrayList<parseStockInfoModel> getList(Context context){
        requestStockData(context);
        return list;
    }

    @SuppressLint("DefaultLocale")
    private ArrayList<parseStockInfoModel> parseStockData(String response) throws JSONException {
        ArrayList<parseStockInfoModel> titles = new ArrayList<>();
        JSONObject obj = new JSONObject(response);
        JSONObject description = obj.getJSONObject("securities");
        JSONArray data = description.getJSONArray("data");
        for (int i = 0;i < data.length(); i++){
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")) {
                continue;
            }
            Double cost_d = data_next.optDouble(3);
            if(Double.isNaN(cost_d)){}
            else{
                titles.add(new parseStockInfoModel(data_next.getString(9),cost_d.toString(),data_next.getString(0)));
            }

        }
        return titles;
    }
}
