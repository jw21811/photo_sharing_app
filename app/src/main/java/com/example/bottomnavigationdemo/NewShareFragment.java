package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NewShareFrgment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_share, container, false);

        GridLayout gridLayout = rootView.findViewById(R.id.GL_fns); // 获取GridLayout的引用

        // 循环创建并添加ImageView
        for (int i = 0; i < 9; i++) {
            ImageView imageView = new ImageView(requireContext());
            imageView.setImageResource(R.mipmap.img_login); // 设置图片资源
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP); // 设置缩放类型

            // 创建LayoutParams并设置行列属性
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 3); // 行
            params.columnSpec = GridLayout.spec(i % 3); // 列
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.setMargins(4, 4, 4, 4); // 设置边距

            imageView.setLayoutParams(params); // 应用LayoutParams
            gridLayout.addView(imageView); // 将ImageView添加到GridLayout
        }

        return rootView;
    }
}

