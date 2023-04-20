package edu.poh.samsung_project_final.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.ListItemOfStockBinding;

public class StockAdapter extends ListAdapter<stockSearchModel, StockAdapter.StockHolder> {


    public StockAdapter(@NonNull StockComparator stockComparator) {
        super(stockComparator);
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_of_stock, parent, false);
        return new StockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class StockHolder extends RecyclerView.ViewHolder {
        public StockHolder(@NonNull View view) {
            super(view);
        }
        ListItemOfStockBinding binding = ListItemOfStockBinding.bind(itemView);
        @SuppressLint("SetTextI18n")
        private void bind(stockSearchModel item){
            if (item.name_of_stock.length() > 14){
                binding.whichStockIsHere.setText(item.name_of_stock.substring(0,14)+"...");
            }else{
                binding.whichStockIsHere.setText(item.name_of_stock);
            }
            //binding.costOfStockWhichIsHere.setText(item.cost_of_stock);
        }
    }
    public static class StockComparator extends DiffUtil.ItemCallback<stockSearchModel>{

        @Override
        public boolean areItemsTheSame(@NonNull stockSearchModel oldItem, @NonNull stockSearchModel newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull stockSearchModel oldItem, @NonNull stockSearchModel newItem) {
            return oldItem == newItem;
        }
    }

}
