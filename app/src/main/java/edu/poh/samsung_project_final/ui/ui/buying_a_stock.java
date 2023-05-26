package edu.poh.samsung_project_final.ui.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
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
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.data.models.UserInfoModel;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;


public class buying_a_stock extends Fragment {
    private FragmentBuyingAStockBinding binding;
    private final String KEY_ID = "1";
    private NavHostFragment navHostFragment;
    private NavController navController;
    private StockDataViewModel stockDataViewModel;
    private UserViewModel userViewModel;
    private stockSearchViewModel model;
    private String name_of_stock;
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
        stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        model = new ViewModelProvider(this).get(stockSearchViewModel.class);
        userInfoModel = LogoFragment.userInfoModel;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buying_a_stock.super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        id_of_stock= args.getString(KEY_ID);
        model.setAllForBuying(id_of_stock, requireContext(), new DataLoadCallback() {
            @Override
            public void onDataLoaded() {
                for (parseStockInfoModel parse: model.stock_page_list){
                    binding.stockSBuyingCost.setText(parse.stock_page_cost_of_stock);
                    binding.stockSBuyingName.setText(parse.stock_page_name_of_stock);
                    cost_d = Double.parseDouble(parse.stock_page_cost_of_stock);
                    name_of_stock = parse.stock_page_name_of_stock;
                }
            }
        });
        AllID = stockDataViewModel.getALlID();
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
                    }else{
                        model.buyingStock(ans, money, count_int, name_of_stock,id_of_stock,AllID, count,userViewModel,stockDataViewModel,userInfoModel);
                        navController.navigate(R.id.action_buying_a_stock_to_favourites_of_character);
                    }
                }
                catch (StringIndexOutOfBoundsException exception){
                    Toast.makeText(buying_a_stock.this.getActivity(), "В поле «Введите количество акций» вы ничего не ввели", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}