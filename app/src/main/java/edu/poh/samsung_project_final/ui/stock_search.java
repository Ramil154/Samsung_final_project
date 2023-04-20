package edu.poh.samsung_project_final.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.databinding.ActivityMainBinding;
import edu.poh.samsung_project_final.databinding.FragmentStockSearchBinding;


public class stock_search extends Fragment {
    private FragmentStockSearchBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public static stock_search newInstance(){
        return null;
    }
}