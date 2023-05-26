package edu.poh.samsung_project_final.ui.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.FragmentStockSearchBinding;
import edu.poh.samsung_project_final.ui.ui.adapter.StockAdapter;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;


public class stock_search extends Fragment implements StockAdapter.Listener {
    private FragmentStockSearchBinding binding;
    private StockAdapter adapter;
    final String KEY_ID = "1";
    private final String CHECK = "2";
    private final String CHECK_STRING = "false";
    private ArrayList<parseStockInfoModel> final_list = new ArrayList<>();
    private stockSearchViewModel model;
    private final StockAdapter.StockComparator stockComparator = new StockAdapter.StockComparator();
    private NavController navController;
    private NavHostFragment navHostFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockSearchBinding.inflate(inflater, container, false);

        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        model = new ViewModelProvider(getActivity()).get(stockSearchViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model.setLiveDataListForStocks(requireContext());
        initStockRecyclerView();
        setNewListInAdapter();
        updateData();
    }

    private void filter(String text){
        ArrayList<parseStockInfoModel> places = new ArrayList<>();
        for(parseStockInfoModel stock: final_list){
            if(stock.stock_search_name_of_stock.toLowerCase().contains(text.toLowerCase())){
                places.add(stock);
            }
            else if(stock.stock_search_id_of_stock.contains(text)){
                places.add(stock);
            }
        }
        adapter.submitList(places);
    }

    private void setNewListInAdapter(){
        binding.characterStockSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void updateData(){
        model.LiveDataListForStocks.observe(getViewLifecycleOwner(), new Observer<ArrayList<parseStockInfoModel>>() {
            @Override
            public void onChanged(ArrayList<parseStockInfoModel> places) {
                final_list = places;
                adapter.submitList(places);
            }
        });
    }

    private void initStockRecyclerView(){
        binding.stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StockAdapter(stockComparator,this);
        binding.stockRecyclerView.setAdapter(adapter);
    }

    public static stock_search newInstance(){
        return null;
    }

    @Override
    public void onClickNow(parseStockInfoModel item) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID,item.stock_search_id_of_stock);
        bundle.putString(CHECK,CHECK_STRING);
        stock_page fragment = new stock_page();
        fragment.setArguments(bundle);
        System.out.println(item.stock_search_id_of_stock);
        navController.navigate(R.id.action_stock_search_to_stock_page, bundle);
        //fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main,fragment).addToBackStack("").commit();
    }
}