package edu.poh.samsung_project_final.ui.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.data.models.UserInfoModel;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;


public class buying_a_stock extends Fragment {
    private FragmentBuyingAStockBinding binding;
    private final String KEY_ID = "1";
    private NavHostFragment navHostFragment;
    private NavController navController;
    private StockDataViewModel stockDataViewModel;
    private UserViewModel userViewModel;
    private String name_of_stock;
    private String cost_of_stock;
    private String id_of_stock;
    private Double cost_d;
    private List<String> AllID;
    private UserInfoModel userInfoModel;
    public static buying_a_stock newInstance() {
        return null;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBuyingAStockBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        userInfoModel = LogoFragment.userInfoModel;
        return binding.getRoot();
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
            cost_of_stock = cost_d.toString();
            binding.stockSBuyingName.setText(name_of_stock);
            binding.stockSBuyingCost.setText(cost_of_stock + " руб");
        }

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
            }
        });
        queque.add(stringRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buying_a_stock.super.onViewCreated(view, savedInstanceState);
        this.stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        Bundle args = getArguments();
        id_of_stock= args.getString(KEY_ID);
        AllID = stockDataViewModel.getALlID();
        parseStockDataCost(id_of_stock);
        binding.GoToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String count = binding.gettingCountOfStocks.getText().toString();
                    char symbol = count.charAt(0);
                    if (symbol == '0' || symbol == '.' || symbol == ',') {
                        Toast.makeText(buying_a_stock.this.getActivity(), "В поле «Введите количество акций» вы ввели не число или не целое число", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int count_int = Integer.parseInt(count);
                    double ans = cost_d * (double) count_int;
                    double money = userViewModel.userEntity.money;
                    if (ans > money) {
                        Toast.makeText(buying_a_stock.this.getActivity(), "Вы превысили ваш бюджет", Toast.LENGTH_SHORT).show();
                    } else {
                        double balance = money - ans;
                        if (AllID.isEmpty()) {
                            stockDataViewModel.insertStock(new StockEntity(id_of_stock, name_of_stock, count_int, ans));
                            updateAll(ans, balance);
                        } else {
                            boolean flag = false;
                            for (int i = 0; i < AllID.size(); i++) {
                                String id = AllID.get(i);
                                if (id.equals(id_of_stock)) {
                                    flag = true;
                                    double price = stockDataViewModel.getPriceById(id);
                                    Integer quantity = stockDataViewModel.getQuantityById(id);
                                    stockDataViewModel.updateById(id_of_stock, quantity + count_int, price + ans);
                                    updateAll(ans, balance);
                                    break;
                                }
                            }
                            if (!flag) {
                                stockDataViewModel.insertStock(new StockEntity(id_of_stock, name_of_stock, count_int, ans));
                                updateAll(ans, balance);
                            }
                        }
                    }
                }
                catch (StringIndexOutOfBoundsException exception){
                    Toast.makeText(buying_a_stock.this.getActivity(), "В поле «Введите количество акций» вы ничего не ввели", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void updateAll(double ans, double balance){
        userInfoModel.all_stock_price_bought += ans;
        userInfoModel.all_stock_price_online += ans;
        //userViewModel.updateMoney(balance);
        userViewModel.userEntity.money = balance;
        navController.navigate(R.id.action_buying_a_stock_to_favourites_of_character);
    }
}