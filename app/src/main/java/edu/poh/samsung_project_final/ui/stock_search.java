package edu.poh.samsung_project_final.ui;

import static java.util.Collections.*;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.FragmentStockPageBinding;
import edu.poh.samsung_project_final.databinding.FragmentStockSearchBinding;
import edu.poh.samsung_project_final.ui.adapters.StockAdapter;
import edu.poh.samsung_project_final.ui.view_models.stockSearchViewModel;


public class stock_search extends Fragment implements StockAdapter.Listener {
    private FragmentStockSearchBinding binding;
    private StockAdapter adapter;
    final String KEY_ID = "1";
    private final String CHECK = "2";
    private final String CHECK_STRING = "false";
    private final stockSearchViewModel model = new stockSearchViewModel();
    private final StockAdapter.StockComparator stockComparator = new StockAdapter.StockComparator();
    private NavController navController;
    NavHostFragment navHostFragment;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockSearchBinding.inflate(inflater, container, false);

        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestStockData();
        initStockRecyclerView();
        updateData();
    }
    private void requestStockData(){
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities.json";
        RequestQueue queque = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            ArrayList<stockSearchModel> stocks = parseStockData(response);
                                model.LiveDataListForStocks.setValue(stocks);
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
    @SuppressLint("DefaultLocale")
    private ArrayList<stockSearchModel> parseStockData(String response) throws JSONException {
        ArrayList<stockSearchModel> titles = new ArrayList<>();
        JSONObject obj = new JSONObject(response);
        JSONObject description = obj.getJSONObject("securities");
        JSONArray data = description.getJSONArray("data");
        for (int i = 0;i < data.length(); i++){
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")) {
                continue;
            }
            double cost_d = data_next.optDouble(3);
            if(Double.isNaN(cost_d)){}
            else{
                String cost;
                if(cost_d < 10.0){
                    cost = String.format("%.3f",cost_d);
                }
                else if (cost_d < 100.0){
                    cost = String.format("%.2f",cost_d);
                }
                else{
                    cost = String.format("%.1f",cost_d);
                }
                titles.add(new stockSearchModel(data_next.getString(9),cost,data_next.getString(0)));
            }

        }
        return titles;
    }

    private void updateData(){
        model.LiveDataListForStocks.observe(getViewLifecycleOwner(), new Observer<ArrayList<stockSearchModel>>() {
            @Override
            public void onChanged(ArrayList<stockSearchModel> places) {
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
    public void onClickNow(stockSearchModel item) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID,item.id_of_stock);
        bundle.putString(CHECK,CHECK_STRING);
        stock_page fragment = new stock_page();
        fragment.setArguments(bundle);
        navController.navigate(R.id.action_stock_search_to_stock_page, bundle);
        //fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main,fragment).addToBackStack("").commit();
    }
}