package org.looa.sharedelement;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ran on 2017/1/1.
 */

public class SimpleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<String> data;

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_item);
        view.setOnClickListener(this);
        Holder holder = new Holder(view);
        holder.setTvTitle(tvTitle);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((Holder) holder).getTvTitle().setText(data.get(position));
        ((Holder) holder).itemView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) listener.onItemClick(v, (Integer) v.getTag());
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    class Holder extends RecyclerView.ViewHolder {

        private TextView tvTitle;

        public Holder(View itemView) {
            super(itemView);
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public void setTvTitle(TextView tvTitle) {
            this.tvTitle = tvTitle;
        }
    }
}
