package edu.poh.samsung_project_final.ui;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.databinding.FragmentMainListOfAppBinding;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;


public class main_list_of_app extends Fragment {
    private FragmentMainListOfAppBinding binding;
    private NavHostFragment navHostFragment;
    private UserViewModel userViewModel;
    private NavController navController;
    private StockDataViewModel stockDataViewModel;
    private List<Double> prices_of_stocks_when_bought;
    private double all_prices_of_stock_online;
    private double all_price_of_stock_when_bought;
    public static main_list_of_app newInstance() {return null;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainListOfAppBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        stockDataViewModel = new ViewModelProvider(getActivity()).get(StockDataViewModel.class);
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onChanged(UserEntity userEntity) {
                prices_of_stocks_when_bought = stockDataViewModel.getPricesOfAllStocks();
                for (int i = 0; i < prices_of_stocks_when_bought.size(); i++){
                    all_price_of_stock_when_bought += prices_of_stocks_when_bought.get(i);
                }
                if(userEntity == null){
                    binding.userProfileMain.setText("Здравствуйте, ");
                }
                else{
                    binding.userProfileMain.setText("Здравствуйте, " + userEntity.login);
                    binding.sumOfStocks.setText(String.format("%.2f",userEntity.money + all_price_of_stock_when_bought) + " руб");
                    Log.d("UserMoney",String.valueOf(userEntity.money)+" main");
                }
                stockDataViewModel.getIdOfStock().observe(getViewLifecycleOwner(), new Observer<List<StockEntity>>() {
                    @Override
                    public void onChanged(List<StockEntity> stockEntities) {
                        for (int i = 0; i < stockEntities.size(); i++){
                            StockEntity stock = stockEntities.get(i);
                            parseStockDataCost(stock.id_of_stock,stock.quantity_of_stock_ent);
                        }
                    }
                });
            }
        });
        double plus_money = all_prices_of_stock_online - all_price_of_stock_when_bought;
        binding.plusSumForStocks.setText((plus_money >= 0 ? "+ ": "- ") + String.format("%.2f",abs(plus_money)));
        if(all_price_of_stock_when_bought == 0){
            binding.percentMain.setText("+ 0.00");
        }
        else {
            double percent_of_diff = ((all_prices_of_stock_online - all_price_of_stock_when_bought) / all_price_of_stock_when_bought) * 100.0;
            binding.percentMain.setText((percent_of_diff >= 0 ? "+ ": "- ") + String.format("%.2f",abs(percent_of_diff)));
        }
        all_price_of_stock_when_bought = 0;
        all_prices_of_stock_online = 0;
        binding.GoToFavouritesFromMainByTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_main_list_of_app_to_favourites_of_character);
            }
        });

    }
    private void parseStockDataCost(String sid,Integer quantity) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queque = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setStockAndCost(response,quantity);
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
    private void setStockAndCost(String response,Integer quantity) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        for (int i = 0; i < data.length();i++){
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")){
                continue;
            }
            double cost_d = data_next.optDouble(3);
            Double cost_final = cost_d*quantity;
            all_prices_of_stock_online += cost_final;
        }

    }
}