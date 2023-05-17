package edu.poh.samsung_project_final.ui.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.poh.samsung_project_final.ui.adapters.data.models.ValutesModel;

public class ValutesViewModel extends ViewModel {
    public MutableLiveData<List<ValutesModel>> LiveDataListForValutes;

    public ValutesViewModel() {
        LiveDataListForValutes = new MutableLiveData<>();
    }

    public MutableLiveData<List<ValutesModel>> getLiveDataListForStocks() {
        return LiveDataListForValutes;
    }
}
