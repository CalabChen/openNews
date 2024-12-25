package com.example.opennews.ui;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.opennews.R;
import com.example.opennews.db.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView profileName;
    private TextView profileWords;
    private ImageButton qrcodeButton;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchNightMode;
    private TextView cacheSize;
    private TextView songCount;
    private TextView titleAbout;

    private List<String> mp3Files; // 用于存储MP3文件名列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图组件
        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileWords = view.findViewById(R.id.profile_words);
        qrcodeButton = view.findViewById(R.id.qrcode_button);
        switchNightMode = view.findViewById(R.id.switch_night_mode);
        cacheSize = view.findViewById(R.id.cache_size);
        songCount = view.findViewById(R.id.song_count);
        titleAbout = view.findViewById(R.id.title_about);

        // 设置点击事件
        setupQrCodeButton();
        setupNightModeSwitch();
        setupClearCacheClick();
        setupLoadedSongsClick();
        setupAboutClick();

        // 更新歌曲数量
        updateSongCount();

        // 更新视图内容
        updateProfileInfo("小明", "个性签名");

    }

    /**
     * 更新个人资料信息。
     */
    private void updateProfileInfo(String name, String identity) {
        if (profileName != null) {
            profileName.setText(name);
        }
        if (profileWords != null) {
            profileWords.setText(identity);
        }
    }

    private void setupQrCodeButton() {
        qrcodeButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "QR Code button clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void setupNightModeSwitch() {
        // 初始化夜间模式开关的状态
        SharedPreferences prefs = requireActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        boolean nightModeEnabled = prefs.getBoolean("night_mode_pref", false);
        switchNightMode.setChecked(nightModeEnabled);

        switchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 保存夜间模式状态到 SharedPreferences
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("night_mode_pref", isChecked);
            editor.apply();

            // 设置夜间模式
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            // 重新创建 Activity 以应用新的主题
            // requireActivity().recreate();
        });
    }

    private void setupClearCacheClick() {
        View clearCacheContainer = (View) requireView().findViewById(R.id.title_clear_cache).getParent();
        if (clearCacheContainer instanceof LinearLayout) {
            clearCacheContainer.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Clear Cache clicked", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void updateSongCount() {
        mp3Files = new ArrayList<>(Arrays.asList(Constants.SONG_RESOURCES));

        int count = mp3Files.size();
        songCount.setText(String.format("%d 首", count));
    }

    private void setupLoadedSongsClick() {
        View loadedSongsContainer = (View) requireView().findViewById(R.id.title_loaded_songs).getParent();
        if (loadedSongsContainer instanceof LinearLayout) {
            loadedSongsContainer.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Loaded Songs clicked", Toast.LENGTH_SHORT).show()
            );

            // 确保 title_loaded_songs 和 song_count 也能触发点击事件
            loadedSongsContainer.findViewById(R.id.title_loaded_songs).setOnClickListener(v -> showSongsDialog());
            loadedSongsContainer.findViewById(R.id.song_count).setOnClickListener(v -> showSongsDialog());
        }
    }

    private void showSongsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("所有音乐");

        // 使用 ArrayAdapter 来展示歌曲列表
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mp3Files);
        builder.setAdapter(adapter, null);

        builder.setPositiveButton("关闭", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupAboutClick() {
        View aboutContainer = (View) requireView().findViewById(R.id.title_about).getParent();
        if (aboutContainer instanceof LinearLayout) {
            aboutContainer.setOnClickListener(v ->
                    Toast.makeText(getContext(), "About clicked", Toast.LENGTH_SHORT).show()
            );
        }
    }
}