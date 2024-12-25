package com.example.opennews.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opennews.R;
import com.example.opennews.model.hot.HotNewsItem;

import java.util.List;
import java.util.Random;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<HotNewsItem> newsItems;
    //随机封面图
    private static final int[] IMAGE_RESOURCES = {
            R.drawable.news1,
            R.drawable.news2,
            R.drawable.news3,
            R.drawable.news4,
            R.drawable.news5
    };


    public NewsAdapter(Context context, List<HotNewsItem> newsItems) {
        this.context = context;
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    public void updateItems(List<HotNewsItem> items) {
        this.newsItems.clear();
        if (items != null) {
            this.newsItems.addAll(items);
        }
        notifyDataSetChanged(); // 通知适配器数据已更改，需要刷新视图
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        HotNewsItem item = newsItems.get(position);

        // 使用默认图标
        //holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        setRandomImage(holder.imageView);
        holder.imageView.setOnClickListener(v -> openWebView(item.getUrl()));

        holder.title.setText(item.getTitle());
        holder.title.setOnClickListener(v -> openWebView(item.getUrl()));

        if (item.getLabel() != null && !item.getLabel().isEmpty()) {
            holder.label.setText(item.getLabel());
            holder.label.setVisibility(View.VISIBLE);
        }

        if (item.getHotValue() != null && !item.getHotValue().isEmpty()) {
            holder.hotValue.setText("热度: " + item.getHotValue());
            holder.hotValue.setVisibility(View.VISIBLE);
        }

       /* if (item.getLabelDesc() != null && !item.getLabelDesc().isEmpty()) {
            holder.labelDesc.setText("标签说明: " + item.getLabelDesc());
            holder.labelDesc.setVisibility(View.VISIBLE);
        } else {
            holder.labelDesc.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        //return newsItems.size();
        return newsItems != null ? newsItems.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNewsItems(List<HotNewsItem> newsItems) {
        this.newsItems = newsItems;
        notifyDataSetChanged();
    }

    private void openWebView(String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title, label, hotValue, labelDesc;

        NewsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.news_image);
            title = itemView.findViewById(R.id.news_title);
            label = itemView.findViewById(R.id.news_label);
            hotValue = itemView.findViewById(R.id.news_hot_value);
            labelDesc = itemView.findViewById(R.id.news_label_desc);
        }
    }

    private void setRandomImage(ImageView imageView) {
        Random random = new Random();
        int randomIndex = random.nextInt(IMAGE_RESOURCES.length);
        imageView.setImageResource(IMAGE_RESOURCES[randomIndex]);
    }

}