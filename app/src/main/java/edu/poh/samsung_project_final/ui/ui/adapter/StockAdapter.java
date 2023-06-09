package edu.poh.samsung_project_final.ui.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.ListItemOfStockBinding;

public class StockAdapter extends ListAdapter<parseStockInfoModel, StockAdapter.StockHolder> {
    Listener listener;
    public StockAdapter(@NonNull StockComparator stockComparator, Listener listener) {
        super(stockComparator);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_of_stock, parent, false);
        return new StockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        holder.bind(getItem(position),listener);
    }

    static class StockHolder extends RecyclerView.ViewHolder {
        public StockHolder(@NonNull View view) {
            super(view);
        }
        ListItemOfStockBinding binding = ListItemOfStockBinding.bind(itemView);
        @SuppressLint("SetTextI18n")
        private void bind(parseStockInfoModel item, Listener listener){
            binding.whichStockIsHere.setText(item.stock_search_name_of_stock);
            binding.costOfStockWhichIsHere.setText(item.stock_search_cost_of_stock+" руб");
            binding.cardViewRecycler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickNow(item);
                }
            });
        }
    }
    public static class StockComparator extends DiffUtil.ItemCallback<parseStockInfoModel>{

        @Override
        public boolean areItemsTheSame(@NonNull parseStockInfoModel oldItem, @NonNull parseStockInfoModel newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull parseStockInfoModel oldItem, @NonNull parseStockInfoModel newItem) {
            return oldItem == newItem;
        }
    }
    public interface Listener{
        void onClickNow(parseStockInfoModel item);
    }

}
