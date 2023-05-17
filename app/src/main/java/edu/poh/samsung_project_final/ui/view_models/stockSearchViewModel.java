package edu.poh.samsung_project_final.ui.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import edu.poh.samsung_project_final.ui.adapters.data.models.stockSearchModel;

public class stockSearchViewModel extends ViewModel {
    public MutableLiveData<ArrayList<stockSearchModel>> LiveDataListForStocks;
    public stockSearchViewModel() {
        LiveDataListForStocks = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<stockSearchModel>> getLiveDataListForStocks() {
        return LiveDataListForStocks;
    }
}
