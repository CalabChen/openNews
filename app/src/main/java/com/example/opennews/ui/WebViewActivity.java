package com.example.opennews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.opennews.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private final Map<Integer, Class<? extends Fragment>> fragmentMap = new HashMap<>();
    private Fragment currentFragment;
    private PlaybackPopupWindow playbackPopupWindow;
    private WebView webView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // 隐藏系统标题栏Toolbar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient()); // 确保链接在WebView内打开
        webView.getSettings().setJavaScriptEnabled(true); // 启用JavaScript

        String url = getIntent().getStringExtra("url");
        if (url != null) {
            webView.loadUrl(url);
        }

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish(); // 如果没有可返回的历史记录，则关闭当前活动
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_manage_playback) {
                View anchor = bottomNav.findViewById(item.getItemId());
                showPlaybackPopupWindow(anchor);
                return true;
            } else if (item.getItemId() == R.id.navigation_home) {
                // 当点击首页时，启动 MainActivity 并传递 新闻 数据
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("load_fragment", "news");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // 清除顶部活动，只保留MainActivity
                startActivity(intent);
                finish(); // 结束当前 WebViewActivity
                return true;
            } else {
                Class<? extends Fragment> fragmentClass = fragmentMap.get(item.getItemId());
                if (fragmentClass != null) {
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
    }

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

}