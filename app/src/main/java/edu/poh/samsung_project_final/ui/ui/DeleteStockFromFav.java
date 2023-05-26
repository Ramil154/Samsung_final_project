package edu.poh.samsung_project_final.ui.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.databinding.DeleteStockFromFavBinding;
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.UserInfoModel;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;

public class DeleteStockFromFav extends Fragment {
    DeleteStockFromFavBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private UserViewModel userViewModel;
    private StockDataViewModel stockDataViewModel;
    private stockSearchViewModel model;
    private String id_of_stock;
    private final String KEY_ID = "1";
    private final String COUNT_ID = "3";
    private String name_of_stock;
    private String cost_of_stock;
    private String quantity;
    private Double cost_d;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private UserInfoModel userInfoModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DeleteStockFromFavBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        model = new ViewModelProvider(getActivity()).get(stockSearchViewModel.class);
        stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        userInfoModel = LogoFragment.userInfoModel;
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DeleteStockFromFav.super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        id_of_stock = args.getString(KEY_ID);
        model.setAllForStockPage(id_of_stock, requireContext(), new DataLoadCallback() {
            @Override
            public void onDataLoaded() {
                for (parseStockInfoModel parse: model.stock_page_list){
                    name_of_stock = parse.stock_page_name_of_stock;
                    cost_of_stock = parse.stock_page_cost_of_stock;
                    cost_d = Double.parseDouble(cost_of_stock);
                    binding.stocksDeleteName.setText(name_of_stock);
                    binding.stocksDeleteCost.setText(cost_of_stock);
                }
            }
        });
        quantity = args.getString(COUNT_ID);
        binding.gettingCountOfStocksToDelete.setHint("Количество акций: " + quantity);
        int count_of_fav = Integer.parseInt(quantity);
        binding.DeleteGoToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String count = binding.gettingCountOfStocksToDelete.getText().toString();
                    model.deleteStock(DeleteStockFromFav.this.getActivity(),cost_d,count,id_of_stock,count_of_fav,userViewModel,stockDataViewModel,userInfoModel);
                    navController.navigate(R.id.action_delete_stock_from_fav_to_favourites_of_character);
                }
                catch (StringIndexOutOfBoundsException exception){
                    Toast.makeText(DeleteStockFromFav.this.getActivity(), "В поле «Введите количество акций, которые хотите удалить из избранных» вы ничего не ввели", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
