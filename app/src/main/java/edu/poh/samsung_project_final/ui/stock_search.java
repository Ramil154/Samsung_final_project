package edu.poh.samsung_project_final.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.FragmentStockSearchBinding;
import edu.poh.samsung_project_final.ui.adapters.StockAdapter;


public class stock_search extends Fragment {
    private FragmentStockSearchBinding binding;
    private StockAdapter adapter;
    private StockAdapter.StockComparator stockComparator = new StockAdapter.StockComparator();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initStockRecyclerView();
    }

    private void initStockRecyclerView(){
        binding.stockRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StockAdapter(stockComparator);
        binding.stockRecyclerView.setAdapter(adapter);
        List<stockSearchModel> list = List.of(new stockSearchModel("Yandex"),
                new stockSearchModel("Сбербанк"),
                new stockSearchModel("Алроса"),
                new stockSearchModel("Нижнекамскнефтехим"),
                new stockSearchModel("Газпром"),
                new stockSearchModel("Лукойл"),
                new stockSearchModel("Тинькофф"),
                new stockSearchModel("X 5"),
                new stockSearchModel("Татнефть"),
                new stockSearchModel("Селегдар"),
                new stockSearchModel("Сегежа"),
                new stockSearchModel("Новатек"),
                new stockSearchModel("Газпром"),
                new stockSearchModel("Лукойл"),
                new stockSearchModel("Тинькофф"),
                new stockSearchModel("X 5"),
                new stockSearchModel("Татнефть"),
                new stockSearchModel("Селегдар"),
                new stockSearchModel("Сегежа"),
                new stockSearchModel("Алроса"),
                new stockSearchModel("Нижнекамскнефтехим"),
                new stockSearchModel("Газпром"),
                new stockSearchModel("Лукойл"),
                new stockSearchModel("Тинькофф"),
                new stockSearchModel("X 5"),
                new stockSearchModel("Татнефть"),
                new stockSearchModel("Селегдар"),
                new stockSearchModel("Сегежа"),
                new stockSearchModel("Новатек"),
                new stockSearchModel("Газпром"),
                new stockSearchModel("Лукойл"),
                new stockSearchModel("Тинькофф"),
                new stockSearchModel("X 5"),
                new stockSearchModel("Татнефтьпппппп"),
                new stockSearchModel("Селегдар"));
        adapter.submitList(list);
    }
    public static stock_search newInstance(){
        return null;
    }
}