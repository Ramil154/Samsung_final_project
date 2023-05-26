package edu.poh.samsung_project_final.ui.data;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import kotlin.jvm.internal.markers.KMutableMap;

public interface OnDataLoadedListener {
    void onDataLoaded(ArrayList<parseStockInfoModel> data);
    void onDataLoadedStockPage(List<parseStockInfoModel> list_stock_page, DataLoadCallback callback);
    void onDataLoadedDates(List<String> Dates);
    void onDataLoadedGraphs(List<Entry> graphics);
}
