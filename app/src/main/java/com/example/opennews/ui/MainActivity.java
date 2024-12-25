package com.example.opennews.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.opennews.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final Map<Integer, Class<? extends Fragment>> fragmentMap = new HashMap<>();
    private Fragment currentFragment;
    private PlaybackPopupWindow playbackPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 恢复夜间模式状态
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean nightModeEnabled = prefs.getBoolean("night_mode_pref", false);
        AppCompatDelegate.setDefaultNightMode(
                nightModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // 隐藏系统标题栏Toolbar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化Fragment容器
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        // 初始化fragmentMap
        fragmentMap.put(R.id.navigation_home, NewsFragment.class);
        fragmentMap.put(R.id.navigation_profile, ProfileFragment.class);

        // 检查是否有来自 WebViewActivity 的额外数据
        String loadFragment = getIntent().getStringExtra("load_fragment");
        if ("news".equals(loadFragment)) {
            // 如果有额外数据且为 "news"，则加载 NewsFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NewsFragment())
                    .commit();
            currentFragment = new NewsFragment();
        } else if (savedInstanceState == null) {
            // 默认加载首页Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NewsFragment())
                    .commit();
            currentFragment = new NewsFragment();
        }


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_manage_playback) { // “播放”按钮的ID
                View anchor = bottomNav.findViewById(item.getItemId());
                showPlaybackPopupWindow(anchor);
                return true;
            } else {
                Class<? extends Fragment> fragmentClass = fragmentMap.get(item.getItemId());
                if (fragmentClass != null && item.getItemId() == R.id.navigation_home) {
                    // 点击首页时，加载 NewsFragment
                    loadFragment(new NewsFragment());
                    return true;
                } else if (fragmentClass != null) {
                    try {
                        Fragment fragment = fragmentClass.getDeclaredConstructor().newInstance();
                        if (!fragment.equals(currentFragment)) {
                            loadFragment(fragment);
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        });

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });

    }

    // 加载fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        currentFragment = fragment;
    }

    private void showPlaybackPopupWindow(View anchor) {
        if (playbackPopupWindow == null) {
            playbackPopupWindow = new PlaybackPopupWindow(this);
        }

        // Calculate the position for PopupWindow to appear above the anchor view.
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int anchorHeight = anchor.getHeight();

        // Adjust the Y offset to place the PopupWindow directly above the anchor view
        playbackPopupWindow.showAtLocation(anchor, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, location[1] - anchorHeight);
    }

    private void performSearch(String query) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof NewsFragment) {
            ((NewsFragment) fragment).filterNews(query);
        }
    }
}