package edu.poh.samsung_project_final.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
import edu.poh.samsung_project_final.data.models.UserInfoModel;
import edu.poh.samsung_project_final.databinding.DeleteStockFromFavBinding;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;

public class DeleteStockFromFav extends Fragment {
    DeleteStockFromFavBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private UserViewModel userViewModel;
    private StockDataViewModel stockDataViewModel;
    private String id_of_stock;
    private final String KEY_ID = "1";
    private final String COUNT_ID = "3";
    private String name_of_stock;
    private String cost_of_stock;
    private String quantity;
    private double cost_d;
    private UserInfoModel userInfoModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DeleteStockFromFavBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        userInfoModel = LogoFragment.userInfoModel;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DeleteStockFromFav.super.onViewCreated(view, savedInstanceState);
        this.stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        Bundle args = getArguments();
        id_of_stock = args.getString(KEY_ID);
        quantity = args.getString(COUNT_ID);
        binding.gettingCountOfStocksToDelete.setHint("Количество акций: " + quantity);
        int count_of_fav = Integer.parseInt(quantity);
        parseStockDataCost(id_of_stock);
        binding.DeleteGoToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String count = binding.gettingCountOfStocksToDelete.getText().toString();
                    char symbol = count.charAt(0);
                    if (symbol == '0' || symbol == '.' || symbol == ','){
                        Toast.makeText(DeleteStockFromFav.this.getActivity(), "В поле «Введите количество акций» вы ввели не число или не целое число", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int count_int = Integer.parseInt(count);
                    double ans = cost_d * count_int;
                    if (count_int > count_of_fav){
                        Toast.makeText(DeleteStockFromFav.this.getActivity(), "Вы указали количество акций большее, чем у вас в избранных", Toast.LENGTH_SHORT).show();
                    }
                    else if(count_int == count_of_fav){
                        stockDataViewModel.deleteById(id_of_stock);
                        updateUserMoneyNow(ans);
                    }
                    else{
                        double price = stockDataViewModel.getPriceById(id_of_stock);
                        stockDataViewModel.updateById(id_of_stock,(count_of_fav - count_int),price - ans);
                        updateUserMoneyNow(ans);
                    }
                    userInfoModel.all_stock_price_online -= ans;
                    userInfoModel.all_stock_price_bought -= ans;
                }
                catch (StringIndexOutOfBoundsException exception){
                    Toast.makeText(DeleteStockFromFav.this.getActivity(), "В поле «Введите количество акций, которые хотите удалить из избранных» вы ничего не ввели", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void updateUserMoneyNow(double ans){
        userViewModel.updateMoney(ans + userViewModel.getMoney());
        navController.navigate(R.id.action_delete_stock_from_fav_to_favourites_of_character);
    }
    private void parseStockDataCost(String sid) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queque = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setStockAndCost(response);
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
    private void setStockAndCost(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        for (int i = 0; i < data.length();i++){
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")){
                continue;
            }
            name_of_stock = data_next.getString(9);
            cost_d = data_next.optDouble(3);
            if(cost_d < 10.0){
                cost_of_stock = String.format("%.3f",cost_d);
            }
            else if (cost_d < 100.0){
                cost_of_stock = String.format("%.2f",cost_d);
            }
            else{
                cost_of_stock = String.format("%.1f",cost_d);
            }
            binding.stocksDeleteName.setText(name_of_stock);
            binding.stocksDeleteCost.setText(cost_of_stock + " руб");
        }

    }

}
