package com.chad.baserecyclerviewadapterhelper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.baserecyclerviewadapterhelper.base.BaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public abstract class BasePagingRefreshingActivity<T> extends BaseActivity {
    protected static final int REQUEST_LOAD_FIRST_PAGE = 1000;
    protected static final int REQUEST_LOAD_MORE_PAGE = 1001;

    protected static final int PAGE_SIZE = 6;

    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected BaseQuickAdapter mAdapter;

    private View noDataView;
    private View errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        noDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent(), false);
        noDataView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());

                refresh();
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup) mRecyclerView.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());

                refresh();
            }
        });

        initAdapter();
        initRefreshLayout();
        mSwipeRefreshLayout.setRefreshing(true);
        refresh();
    }

    protected abstract BaseQuickAdapter getPullToRefreshAdapter();

    private void initAdapter() {
        mAdapter = getPullToRefreshAdapter();
        mAdapter.setHeaderFooterEmpty(true, true);

        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mRecyclerView);

//        mAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
//        mAdapter.setPreLoadNumber(3);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    protected abstract void fetchFirstPage();

    protected abstract void fetchMorePage();

    protected void handleFirstPageResponse(List<T> data) {
        setData(true, data);
        mAdapter.setEnableLoadMore(true);
        mSwipeRefreshLayout.setRefreshing(false);

        if (data.isEmpty()) {
            //显示暂无数据
            mAdapter.setEmptyView(noDataView);
        }
    }

    protected void handleFirstPageError() {
        mAdapter.setEnableLoadMore(true);
        mSwipeRefreshLayout.setRefreshing(false);

        mAdapter.setEmptyView(errorView);
    }

    protected void handleMorePageResponse(List<T> data) {
        setData(false, data);
    }

    protected void handleMorePageError() {
        mAdapter.loadMoreFail();
    }

    protected void refresh() {
        mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
        fetchFirstPage();
    }

    private void loadMore() {
        fetchMorePage();
    }

    private void setData(boolean isRefresh, List data) {
        final int size = data == null ? 0 : data.size();
        if (isRefresh) {
            mAdapter.resetData(data);
        } else {
            if (size > 0) {
                mAdapter.addData(data);
            }
        }
        if (size < PAGE_SIZE) {
            //第一页如果不够一页就不显示没有更多数据布局
            mAdapter.loadMoreEnd(isRefresh);
        } else {
            mAdapter.loadMoreComplete();
        }
    }
}