package com.easemob.chatuidemo.bqmmgif;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.melink.baseframe.utils.DensityUtils;
import com.melink.bqmmsdk.bean.BQMMGif;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by syh on 07/12/2017.
 */

public class BQMMSearchPopupWindow extends PopupWindow {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private BQMMSearchContentAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private int totalItemCount;
    private int lastVisiableItemPosition;
    private LoadMoreListener mLoadMoreListener;
    private int[] mParentLocation = new int[]{0, 0};
    private WeakReference<View> mParentViewWeakReference;

    public BQMMSearchPopupWindow(Context context, int height) {
        super();
        mContext = context;
        mRecyclerView = new RecyclerView(context);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new BQMMSearchContentAdapter();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = mLinearLayoutManager.getItemCount();
                lastVisiableItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisiableItemPosition + 2)) {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.loadMore();
                    }
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        setContentView(mRecyclerView);
        setHeight(height);
        setFocusable(false);
    }

    public void setParentView(View parent) {
        parent.getLocationOnScreen(mParentLocation);
        mParentViewWeakReference = new WeakReference<>(parent);
    }

    public void show(final List<BQMMGif> stickers) {
        mRecyclerView.scrollToPosition(0);
        if (mParentViewWeakReference != null) {
            final View parent = mParentViewWeakReference.get();
            if (parent != null) {
                if (isShowing()) dismiss();
                mAdapter.setMMWebStickerList(stickers);
                int pixels = DensityUtils.dip2px(90);
                int contentWidth = stickers.size() * pixels;
                int screenWidth = DensityUtils.getScreenW();
                int width = contentWidth > screenWidth ? screenWidth : contentWidth;
                setWidth(width);
                showAtLocation(parent, Gravity.NO_GRAVITY, mParentLocation[0] + parent.getWidth() - getWidth(), mParentLocation[1] - getHeight());
            }
        }
    }

    public void showMore(final List<BQMMGif> stickers) {
        mAdapter.addMMWebStickerList(stickers);
    }

    public void setLoadMoreListener(LoadMoreListener mLoadMoreListener) {
        this.mLoadMoreListener = mLoadMoreListener;
    }

    public BQMMSearchContentAdapter getAdapter() {
        return mAdapter;
    }

    interface LoadMoreListener {
        void loadMore();
    }
}
