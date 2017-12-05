package com.chad.baserecyclerviewadapterhelper.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.baserecyclerviewadapterhelper.R;
import com.orhanobut.logger.Logger;

/**
 * 文 件 名: BaseActivity
 * 创 建 人: Allen
 * 创建日期: 16/12/24 15:33
 * 邮   箱: AllenCoder@126.com
 * 修改时间：
 * 修改备注：
 */
public class BaseActivity extends AppCompatActivity {
    /**
     * 日志输出标志getSupportActionBar().
     **/
    private TextView mTitle;
    private ImageView mBack;
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public void setTitle(CharSequence msg) {
        if (mTitle != null) {
            mTitle.setText(msg);
        }
    }

    /**
     * sometime you want to define mBack event
     */
    protected void showBackButton() {
        if (mBack != null) {
            mBack.setVisibility(View.VISIBLE);
            mBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            Logger.t(TAG).e("mBack is null ,please check out");
        }

    }

    protected void setBackClickListener(View.OnClickListener l) {
        if (mBack != null) {
            mBack.setVisibility(View.VISIBLE);
            mBack.setOnClickListener(l);
        } else {
            Logger.t(TAG).e("mBack is null ,please check out");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 经测试在代码里直接声明透明状态栏更有效
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 这句很关键，注意是调用父类的方法
        super.setContentView(R.layout.activity_base);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (getSupportActionBar() != null) {
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }
        mBack = (ImageView) findViewById(R.id.img_back);
        mTitle = (TextView) findViewById(R.id.title);

        //默认不显示back按钮
        mBack.setVisibility(View.GONE);
    }


    @Override
    public void setContentView(int layoutId) {
        setContentView(View.inflate(this, layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        FrameLayout frameLayout = findViewById(R.id.content);
        frameLayout.removeAllViews();

        frameLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initToolbar();
    }
}
