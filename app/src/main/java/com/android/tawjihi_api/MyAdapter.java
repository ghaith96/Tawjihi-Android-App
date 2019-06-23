package com.android.tawjihi_api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static ArrayList<ResultInfo> mDataset;
    private Context context;

    public MyAdapter(ArrayList<ResultInfo> p0, Context context) {
        mDataset = p0;
        this.context = context;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull final MyAdapter.ViewHolder holder, final int position) {
        ResultInfo info = mDataset.get(position);
        holder.name.setText(info.name);
        holder.school.setText(info.school);
        holder.score.setText(info.score);
        holder.region.setText(info.region);
        holder.branch.setText(info.branch);
        holder.year.setText(info.year);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultInfo info = mDataset.get(position);
                Intent intent = new Intent(context, StatsActivity.class);
                intent.putExtra("ResultInfoObject", info);

                Pair<View, String> p1 = Pair.create(holder.branch_img, holder.branch_img.getTransitionName());
                Pair<View, String> p2 = Pair.create(holder.branch, holder.branch.getTransitionName());
                Pair<View, String> p3 = Pair.create(holder.name, holder.name.getTransitionName());
                Pair<View, String> p5 = Pair.create(holder.score, holder.score.getTransitionName());

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                        p1, p2, p3, p5);
                context.startActivity(intent, options.toBundle());
            }
        });

        // holder.id.setText(info.id);

        // if (info.score == null || info.score.equals("")) {
        // info.score = "0";
        // }
        if (info.name == null || info.name.equals("")) {
            info.name = "الاسم غير متوفر";
        }
        if (info.score.isEmpty())
            info.score = "0";
        float _score = Float.parseFloat(info.score);
        if (_score > 90) // green
        {
            holder.score.setTextColor(ContextCompat.getColor(context, R.color.veryGood));
        } else if (_score > 80) // yellow
        {
            holder.score.setTextColor(ContextCompat.getColor(context, R.color.good));
        } else if (_score > 70)// orange
        {
            holder.score.setTextColor(ContextCompat.getColor(context, R.color.notBad));
        } else { // red
            holder.score.setTextColor(ContextCompat.getColor(context, R.color.fail));
        }

        switch (info.branch) {
        case "العلمي":
            holder.branch_img.setImageResource(R.drawable.ic_flask);
            break;

        case "الأدبي":
            holder.branch_img.setImageResource(R.drawable.ic_book);
            break;
        case "الادبي":
            holder.branch_img.setImageResource(R.drawable.ic_book);
            break;
        case "العلوم الإنسانية":
            holder.branch_img.setImageResource(R.drawable.ic_book);
            break;
        case "العلوم الانسانية":
            holder.branch_img.setImageResource(R.drawable.ic_book);
            break;

        case "الفندقي":
            holder.branch_img.setImageResource(R.drawable.ic_hotel);
            break;
        case "تطبيقي فندقي":
            holder.branch_img.setImageResource(R.drawable.ic_hotel);
            break;

        case "الزراعي":
            holder.branch_img.setImageResource(R.drawable.ic_planting);
            break;
        case "الزراعي التطبيقي":
            holder.branch_img.setImageResource(R.drawable.ic_planting);
            break;
        case "الزراعي المهني":
            holder.branch_img.setImageResource(R.drawable.ic_planting);
            break;
        case "تطبيقي زراعي":
            holder.branch_img.setImageResource(R.drawable.ic_planting);
            break;

        case "التجاري":
            holder.branch_img.setImageResource(R.drawable.ic_shop);
            break;
        case "الريادة والأعمال":
            holder.branch_img.setImageResource(R.drawable.ic_shop);
            break;
        case "تطبيقي اقتصاد منزلي":
            holder.branch_img.setImageResource(R.drawable.ic_shop);
            break;
        case "الاقتصاد المنزلي التطبيقي":
            holder.branch_img.setImageResource(R.drawable.ic_shop);
            break;
        case "الاقتصاد المنزلي":
            holder.branch_img.setImageResource(R.drawable.ic_shop);
            break;

        case "الصناعي":
            holder.branch_img.setImageResource(R.drawable.ic_tools);
            break;
        case "الصناعي التطبيقي":
            holder.branch_img.setImageResource(R.drawable.ic_tools);
            break;
        case "تطبيقي صناعي":
            holder.branch_img.setImageResource(R.drawable.ic_tools);
            break;

        case "الشرعي":
            holder.branch_img.setImageResource(R.drawable.ic_islam);
            break;

        }
        // Animation animation = AnimationUtils.loadAnimation(context,
        // android.R.anim.fade_in);
        // holder.itemView.startAnimation(animation);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mCardView;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.school)
        TextView school;
        @BindView(R.id.rating)
        TextView score;
        @BindView(R.id.region)
        TextView region;
        @BindView(R.id.branch)
        TextView branch;
        @BindView(R.id.year)
        TextView year;
        @BindView(R.id.branch_img)
        ImageView branch_img;

        public ViewHolder(CardView itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mCardView = itemView;
        }
    }

}
