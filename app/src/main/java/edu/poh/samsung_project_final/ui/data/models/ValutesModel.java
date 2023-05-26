package edu.poh.samsung_project_final.ui.data.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.poh.samsung_project_final.ui.data.DataLoadCallback;

public class ValutesModel {
    private final List<String> TEXT = Arrays.asList("Канадский доллар","Швейцарский франк","Китайский юань","Евро","Фунт стерлингов",
            "Гонконгский доллар","Японская йена","Турецкая лира","Доллар США");
    private  OnDataLoadedListenerMainFr listener;
    public String name_valute;
    public String id_valute;
    public String cost_valute;

    public ValutesModel(){}

    public ValutesModel(String name_valute,String id_valute,String cost_valute){
        this.name_valute = name_valute;
        this.id_valute = id_valute;
        this.cost_valute = cost_valute;
    }

    public void setListener(OnDataLoadedListenerMainFr listener) {
        this.listener = listener;
    }

    public interface OnDataLoadedListenerMainFr {
        void onDataLoadedValutes(List<ValutesModel> list_stock_page,DataLoadCallback callback);
        void onDataLoadedCostOnline(double cost_online, DataLoadCallback callback);
    }

    public void parseValuteData(Context context, DataLoadCallback callback) {
        String url = "https://iss.moex.com/iss/statistics/engines/futures/markets/indicativerates/securities.json";
        RequestQueue queque = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<ValutesModel> stocks = setValute(response);
                            listener.onDataLoadedValutes(stocks, callback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queque.add(stringRequest);
    }

    @SuppressLint("DefaultLocale")
    private List<ValutesModel> setValute(String response) throws JSONException {
        List<ValutesModel> titles = new ArrayList<>();
        JSONObject obj = new JSONObject(response);
        JSONObject description = obj.getJSONObject("securities");
        JSONArray data = description.getJSONArray("data");
        for (int i = 0;i < data.length(); i++){
            JSONArray valute = data.getJSONArray(i);
            Double cost_d = valute.optDouble(3);
            if(Double.isNaN(cost_d)){}
            else{
                String cost = cost_d.toString();
                titles.add(new ValutesModel(TEXT.get(i),valute.getString(2),cost));
                Log.d("Valutes", titles.size() + " ");
            }
        }
        return titles;
    }

    public void parseStockDataCostMain(Context context,String sid,Integer quantity, DataLoadCallback callback) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queque = Volley.newRequestQueue(context);
        final double[] all_prices_of_stock_online = {0};
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            all_prices_of_stock_online[0] = setStockAndCostMain(response,quantity);
                            listener.onDataLoadedCostOnline(all_prices_of_stock_online[0], callback);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queque.add(stringRequest);
    }
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private double setStockAndCostMain(String response,Integer quantity) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        double all_prices_of_stock_online = 0;
        for (int i = 0; i < data.length(); i++) {
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")) {
                continue;
            }
            double cost_d = data_next.optDouble(3);
            double cost_final = cost_d * quantity;
            all_prices_of_stock_online += cost_final;
        }
        return all_prices_of_stock_online;
    }
}
