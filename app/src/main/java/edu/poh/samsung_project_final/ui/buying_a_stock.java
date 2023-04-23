package edu.poh.samsung_project_final.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.databinding.FragmentStockPageBinding;

public class buying_a_stock extends Fragment {
    private FragmentBuyingAStockBinding binding;
    public static buying_a_stock newInstance() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBuyingAStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}