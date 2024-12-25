package com.example.opennews.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.opennews.R;
import com.example.opennews.api.HotNewsApiService;
import com.example.opennews.db.NewsDbHelper;
import com.example.opennews.model.hot.HotNewsItem;
import com.example.opennews.model.hot.HotNewsResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<HotNewsItem> newsItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 从数据库读取新闻数据
        loadNewsDataFromDatabase();

        // 从API获取新闻数据并保存到数据库
        fetchNewsData();

        return view;
    }

    private void fetchNewsData() {
        new Thread(() -> {
            HotNewsResponse response = HotNewsApiService.fetchHotNews();
            if (response.isSuccess() && response.getData() != null) {
                NewsDbHelper dbHelper = new NewsDbHelper(getContext());
                dbHelper.insertAll(response.getData());
                requireActivity().runOnUiThread(() -> {
                    loadNewsDataFromDatabase();
                });
            }
        }).start();
    }

    private void loadNewsDataFromDatabase() {
        NewsDbHelper dbHelper = new NewsDbHelper(getContext());
        newsItems.clear();
        newsItems.addAll(dbHelper.getAllHotNewsItems());
        Log.d("NewsFragment", "Loaded " + newsItems.size() + " news items.");
        adapter.setNewsItems(newsItems);
        adapter.notifyDataSetChanged();
    }

    /**
     * 根据提供的查询字符串过滤新闻列表。
     */
    @SuppressLint("NotifyDataSetChanged")
    public void filterNews(String query) {
        List<HotNewsItem> filteredList = new ArrayList<>();
        Set<String> addedTitles = new HashSet<>(); // 用于跟踪已添加的标题

        if (query == null || query.trim().isEmpty()) {
            // 如果查询为空，则显示所有新闻条目
            filteredList.addAll(newsItems);
        } else {
            for (HotNewsItem item : newsItems) {
                String title = item.getTitle();
                if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                    // 只有当标题未被添加时才加入列表
                    if (!addedTitles.contains(title)) {
                        filteredList.add(item);
                        addedTitles.add(title); // 标记此标题已被添加
                    }
                }
            }
        }
        adapter.setNewsItems(filteredList);
        adapter.notifyDataSetChanged();
    }

}