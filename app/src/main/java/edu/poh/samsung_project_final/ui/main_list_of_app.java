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
import androidx.recyclerview.widget.LinearLayoutManager;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.ui.adapters.data.models.UserInfoModel;
import edu.poh.samsung_project_final.ui.adapters.data.models.ValutesModel;
import edu.poh.samsung_project_final.databinding.FragmentMainListOfAppBinding;
import edu.poh.samsung_project_final.ui.adapters.ValutesAdapter;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.view_models.ValutesViewModel;


public class main_list_of_app extends Fragment {
    private FragmentMainListOfAppBinding binding;
    private NavHostFragment navHostFragment;
    private UserViewModel userViewModel;
    private ValutesAdapter adapter;
    private NavController navController;
    private ValutesViewModel model = new ValutesViewModel();
    private StockDataViewModel stockDataViewModel;
    private UserInfoModel userInfoModel;
    private RequestQueue requestQueue;
    private double all_prices_of_stock_online;
    private final List<String> TEXT = Arrays.asList("Канадский доллар","Швейцарский франк","Китайский юань","Евро","Фунт стерлингов",
            "Гонконгский доллар","Японская йена","Турецкая лира","Доллар США");

    public static main_list_of_app newInstance() {return null;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainListOfAppBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        stockDataViewModel = new ViewModelProvider(getActivity()).get(StockDataViewModel.class);
        userInfoModel = LogoFragment.userInfoModel;
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.plusSumForStocks.setText(String.format("%.2f",userInfoModel.all_stock_price_online - userInfoModel.all_stock_price_bought) + " руб");
        SumPrecent();
        requestQueue = Volley.newRequestQueue(requireContext());
        parseValuteData();
        initValuteRecyclerView();
        setUpObservers();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setUpObservers(){
        all_prices_of_stock_online = 0;
        final boolean[] flag = {false};
        binding.userProfileMain.setText("Здравствуйте, " + userViewModel.userEntity.login);
        binding.sumOfStocks.setText(String.format("%.2f",userViewModel.userEntity.money + userInfoModel.all_stock_price_online) + " руб");
        List<Double> prices_of_stocks_when_bought = stockDataViewModel.getPricesOfAllStocks();
        double all_price_of_stock_when_bought = 0;
        for (int i = 0; i < prices_of_stocks_when_bought.size(); i++) {
            all_price_of_stock_when_bought += prices_of_stocks_when_bought.get(i);
        }
        double finalAll_price_of_stock_when_bought = all_price_of_stock_when_bought;
        stockDataViewModel.getIdOfStock().observe(getViewLifecycleOwner(), new Observer<List<StockEntity>>() {
            @Override
            public void onChanged(List<StockEntity> stockEntities) {
                for (int i = 0; i < stockEntities.size(); i++) {
                    StockEntity stock = stockEntities.get(i);
                    parseStockDataCost(stock.id_of_stock, stock.quantity_of_stock_ent);
                }
                if (!flag[0]) {
                    new android.os.Handler().postDelayed(new Runnable() {
                        @SuppressLint({"DefaultLocale", "SetTextI18n"})
                        @Override
                        public void run() {
                            if (userInfoModel.all_stock_price_online != all_prices_of_stock_online) {
                                if (userInfoModel.all_stock_price_bought != finalAll_price_of_stock_when_bought) {
                                    userInfoModel.all_stock_price_bought = finalAll_price_of_stock_when_bought;
                                }
                                userInfoModel.all_stock_price_online = all_prices_of_stock_online;
                                binding.sumOfStocks.setText(String.format("%.2f", userViewModel.userEntity.money + userInfoModel.all_stock_price_online) + " руб");
                                binding.plusSumForStocks.setText(String.format("%.2f", userInfoModel.all_stock_price_online - userInfoModel.all_stock_price_bought) + " руб");
                                SumPrecent();
                            }
                        }
                    }, 2000);
                    flag[0] = true;
                }

            }
        });
        updateData();
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void SumPrecent(){
        if(userInfoModel.all_stock_price_bought == 0 || (abs(userInfoModel.all_stock_price_online-userInfoModel.all_stock_price_bought) <  0.00001)){
            binding.percentMain.setText("+ 0.00 %");
        }
        else {
            double percent_of_diff = ((userInfoModel.all_stock_price_online - userInfoModel.all_stock_price_bought) / userInfoModel.all_stock_price_bought) * 100.0;
            binding.percentMain.setText((percent_of_diff >= 0 ? "+ ": "- ") + String.format("%.2f",abs(percent_of_diff)) + " %");
        }
    }

    private void parseStockDataCost(String sid,Integer quantity) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
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
            }
        });
        requestQueue.add(stringRequest);
    }
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setStockAndCost(String response,Integer quantity) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
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
    }

    private void parseValuteData() {
        String url = "https://iss.moex.com/iss/statistics/engines/futures/markets/indicativerates/securities.json";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<ValutesModel> stocks = setValute(response);
                            model.LiveDataListForValutes.postValue(stocks);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
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
            }
        }
        return titles;
    }

    private void initValuteRecyclerView(){
        binding.valutesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ValutesAdapter();
        binding.valutesRecyclerView.setAdapter(adapter);
        ValutesViewModel valutesViewModel = new ViewModelProvider(this).get(ValutesViewModel.class);
        model = valutesViewModel;
    }

    private void updateData(){
        model.LiveDataListForValutes.observe(getViewLifecycleOwner(), new Observer<List<ValutesModel>>() {
            @Override
            public void onChanged(List<ValutesModel> places) {
                adapter.setList(places);
            }
        });
    }
}