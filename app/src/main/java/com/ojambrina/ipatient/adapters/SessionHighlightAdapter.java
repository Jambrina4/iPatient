package com.ojambrina.ipatient.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ojambrina.ipatient.R;
import com.ojambrina.ipatient.entities.Session;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SessionHighlightAdapter extends RecyclerView.Adapter<SessionHighlightAdapter.ViewHolder> {

    private Context context;
    private Session session;
    private String highlight;
    private List<String> highlightList = new ArrayList<>();

    public SessionHighlightAdapter(Context context, Session session) {
        this.context = context;
        this.session = session;
        highlightList.clear();
        highlightList.addAll(session.getHighlightList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_higlight_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {
        highlight = highlightList.get(holder.getAdapterPosition());
        holder.textDetail.setText(highlight);
    }

    @Override
    public int getItemCount() {
        return highlightList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_detail)
        ImageView imageDetail;
        @BindView(R.id.text_detail)
        TextView textDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}