package edu.poh.samsung_project_final.ui;

import static java.util.Collections.*;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
    private final stockSearchViewModel model = new stockSearchViewModel();
    private final StockAdapter.StockComparator stockComparator = new StockAdapter.StockComparator();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockSearchBinding.inflate(inflater, container, false);
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
                            if (model.LiveDataListForStocks != null) {
                                model.LiveDataListForStocks.setValue(stocks);
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
    private ArrayList<stockSearchModel> parseStockData(String response) throws JSONException {
        ArrayList<stockSearchModel> titles = new ArrayList<>();
        JSONObject obj = new JSONObject(response);
        JSONObject description = obj.getJSONObject("securities");
        JSONArray data = description.getJSONArray("data");
        for (int i = 0;i < data.length(); i++){
            JSONArray data_next = data.getJSONArray(i);
            Double cost_d = data_next.optDouble(15);
            if(Double.isNaN(cost_d)){}
            else{
                titles.add(new stockSearchModel(data_next.getString(9),cost_d.toString(),data_next.getString(0)));
                Log.d("MyLod",cost_d.toString());
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
        stock_page fragment = new stock_page();
        Bundle bundle = new Bundle();
        bundle.putString("1",item.id_of_stock);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main,fragment).addToBackStack("").commit();
    }
}