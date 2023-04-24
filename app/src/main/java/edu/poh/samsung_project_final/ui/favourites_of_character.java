package edu.poh.samsung_project_final.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.databinding.FragmentFavouritesOfCharacterBinding;


public class favourites_of_character extends Fragment {

    private FragmentFavouritesOfCharacterBinding binding;

    public static favourites_of_character newInstance() { return null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesOfCharacterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}