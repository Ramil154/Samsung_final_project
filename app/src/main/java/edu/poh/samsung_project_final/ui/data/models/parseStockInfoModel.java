package edu.poh.samsung_project_final.ui.data.models;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.OnDataLoadedListener;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.ui.buying_a_stock;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;

public class parseStockInfoModel{
    private OnDataLoadedListener listener;
    public String stock_search_cost_of_stock;
    public String stock_search_id_of_stock;
    public String stock_search_name_of_stock;

    public String stock_page_name_of_stock;
    public String stock_page_cost_of_stock;
    public float stock_page_cost_first_in_data;
    public String stock_page_persent_str;
    public String stock_page_colour;

    public parseStockInfoModel(){}

    public parseStockInfoModel(String name_of_stock,String cost_of_stock,String id_of_stock){
        this.stock_search_cost_of_stock = cost_of_stock;
        this.stock_search_name_of_stock = name_of_stock;
        this.stock_search_id_of_stock = id_of_stock;
    }

    public parseStockInfoModel(String name_of_stock,String cost_of_stock, String persent_str, String colour){
        this.stock_page_cost_of_stock = cost_of_stock;
        this.stock_page_name_of_stock = name_of_stock;
        this.stock_page_persent_str = persent_str;
        this.stock_page_colour = colour;
    }

    public interface OnDataLoadedListener {
        void onDataLoaded(ArrayList<parseStockInfoModel> data);
        void onDataLoadedStockPage(List<parseStockInfoModel> list_stock_page, DataLoadCallback callback);
        void onDataLoadedDates(List<String> Dates);
        void onDataLoadedGraphs(List<Entry> graphics);
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

    public void getList(Context context){
        requestStockData(context);
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

    private void getResult(String id, String searchDate, Context context){
        String url = "https://iss.moex.com/iss/history/engines/stock/markets/shares/securities/"+id+".json?from="+searchDate;
        RequestQueue queque = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            parseHistory(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("MyWay","Error");
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

    private void parseHistory(String response) throws JSONException{
        JSONObject obj = new JSONObject(response);
        List<String> Dates_onGraph = new ArrayList<>();
        List<Entry> graphics = new ArrayList<>();
        boolean flag_graph = false;
        JSONObject history = obj.getJSONObject("history");
        JSONArray data = history.getJSONArray("data");
        int counter = 0;
        for (int i = 0; i < data.length(); i++){
            JSONArray mas = data.getJSONArray(i);
            Double cost_history = mas.optDouble(9);
            String date = mas.getString(1);
            if(Double.isNaN(cost_history)){}
            else{
                String cost =  cost_history.toString();
                float cost_f = Float.parseFloat(cost);
                if (!flag_graph){
                    stock_page_cost_first_in_data = cost_f;
                    flag_graph = true;
                }
                graphics.add(new Entry(counter,cost_f));
                counter++;
                Dates_onGraph.add(date);
            }
        }
        listener.onDataLoadedDates(Dates_onGraph);
        listener.onDataLoadedGraphs(graphics);
    }


    private void parseStockDataCost(String sid, Context context, DataLoadCallback callback) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queque = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setStockAndCost(response, callback);
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setStockAndCost(String response, DataLoadCallback callback) throws JSONException {
        List<parseStockInfoModel> stock_page_list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")) {
                continue;
            }
            String name = data_next.getString(9);
            stock_page_name_of_stock = name;
            Double cost_d = data_next.optDouble(3);
            stock_page_cost_of_stock = cost_d.toString();
            double ans = cost_d - stock_page_cost_first_in_data;
            String answer;
            if (cost_d < 5) {
                answer = String.format("%.4f", abs(ans));
            } else {
                answer = String.format("%.2f", abs(ans));
            }
            double percent_of_diff = ((ans) / cost_d) * 100.0;
            if (percent_of_diff < 0.0) {
                stock_page_colour = "#D41307";
                stock_page_persent_str = "- " + answer + " " + String.format("%.2f", abs(percent_of_diff)) + " %";
            } else {
                stock_page_colour = "#07D434";
                stock_page_persent_str = "+ " + answer + " " + String.format("%.2f", abs(percent_of_diff)) + " %";
            }
        }
        stock_page_list.add(new parseStockInfoModel(stock_page_name_of_stock,stock_page_cost_of_stock
                ,stock_page_persent_str,stock_page_colour));
        listener.onDataLoadedStockPage(stock_page_list, callback);
        callback.onDataLoaded();
    }

    public void getAllFroGraph(String id, Context context, DataLoadCallback callback){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String searchDate = year + "-0" + month + "-" + day;
        getResult(id,searchDate,context);
        parseStockDataCost(id, context, callback);
    }

    public void getAllForBuying(String id, Context context, DataLoadCallback callback){
        parseStockDataCost(id,context,callback);
    }

    public void buyingStock(Activity activity,Double cost_d, String name_of_stock, String id_of_stock, List<String> AllID, String count, UserViewModel userViewModel, StockDataViewModel stockDataViewModel,UserInfoModel userInfoModel){
        char symbol = count.charAt(0);
        if (symbol == '0' || symbol == '.' || symbol == ',') {
            Toast.makeText(activity, "В поле «Введите количество акций» вы ввели не число или не целое число", Toast.LENGTH_SHORT).show();
            return;
        }
        int count_int = Integer.parseInt(count);
        double ans = cost_d * (double) count_int;
        double money = userViewModel.userEntity.money;
        if (ans > money) {
            Toast.makeText(activity, "Вы превысили ваш бюджет", Toast.LENGTH_SHORT).show();
        } else {
            double balance = money - ans;
            if (AllID.isEmpty()) {
                stockDataViewModel.insertStock(new StockEntity(id_of_stock, name_of_stock, count_int, ans));
                updateAll(ans, balance, userInfoModel, userViewModel);
            } else {
                boolean flag = false;
                for (int i = 0; i < AllID.size(); i++) {
                    String id = AllID.get(i);
                    if (id.equals(id_of_stock)) {
                        flag = true;
                        double price = stockDataViewModel.getPriceById(id);
                        Integer quantity = stockDataViewModel.getQuantityById(id);
                        stockDataViewModel.updateById(id_of_stock, quantity + count_int, price + ans);
                        updateAll(ans, balance,userInfoModel,userViewModel);
                        break;
                    }
                }
                if (!flag) {
                    stockDataViewModel.insertStock(new StockEntity(id_of_stock, name_of_stock, count_int, ans));
                    updateAll(ans, balance, userInfoModel, userViewModel);
                }
            }
        }
    }

    private void updateAll(double ans, double balance, UserInfoModel userInfoModel, UserViewModel userViewModel){
        userInfoModel.all_stock_price_bought += ans;
        userInfoModel.all_stock_price_online += ans;
        userViewModel.userEntity.money = balance;
    }

    public void deleteStock(Activity activity,Double cost_d, String count, String id_of_stock,int count_of_fav, UserViewModel userViewModel, StockDataViewModel stockDataViewModel,UserInfoModel userInfoModel){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        char symbol = count.charAt(0);
        if (symbol == '0' || symbol == '.' || symbol == ','){
            Toast.makeText(activity, "В поле «Введите количество акций» вы ввели не число или не целое число", Toast.LENGTH_SHORT).show();
            return;
        }
        int count_int = Integer.parseInt(count);
        double ans = cost_d * count_int;
        if (count_int > count_of_fav){
            Toast.makeText(activity, "Вы указали количество акций большее, чем у вас в избранных", Toast.LENGTH_SHORT).show();
        }
        else if(count_int == count_of_fav){
            stockDataViewModel.deleteById(id_of_stock);
            updateUserMoneyNow(ans, userViewModel);
            databaseReference.child("Users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("favourites")
                    .child(id_of_stock)
                    .setValue(null);

        }
        else{
            double price = stockDataViewModel.getPriceById(id_of_stock);
            stockDataViewModel.updateById(id_of_stock,(count_of_fav - count_int),price - ans);
            updateUserMoneyNow(ans,userViewModel);
        }
        userInfoModel.all_stock_price_online -= ans;
        userInfoModel.all_stock_price_bought -= ans;
    }

    private void updateUserMoneyNow(double ans, UserViewModel userViewModel){
        userViewModel.userEntity.money = ans + userViewModel.userEntity.money;
    }
}
