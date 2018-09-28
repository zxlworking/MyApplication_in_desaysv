package com.desay_sv.test_weather.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desay_sv.test_weather.R;
import com.desay_sv.test_weather.custom.view.TodayWeatherView;
import com.desay_sv.test_weather.event.SelectLeftMenuEvent;
import com.desay_sv.test_weather.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/20.
 */

public class LeftMenuFragment extends BaseFragment {
    private static final String TAG = "LeftMenuFragment";

    private static final String[] MENU_TITLE_ARRAY = new String[]{"笑话","美女","账号","检查更新"};

    private View mContentView;

    private TodayWeatherView mTodayWeatherView;
    private RecyclerView mRecyclerView;

    private LeftMenuAdapter mLeftMenuAdapter;

    private Toolbar mToolbar;

    private int mSelectedPosition = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG,"onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_left_menu,null, false);

        mTodayWeatherView = mContentView.findViewById(R.id.left_menu_today_weather_view);
        mRecyclerView = mContentView.findViewById(R.id.left_menu_recycler_view);

        mLeftMenuAdapter = new LeftMenuAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity,2);
//        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mLeftMenuAdapter);

        if(mToolbar != null){
            mTodayWeatherView.setToolbar(mToolbar);
        }

        setSelectedPosition(0);

        return mContentView;
    }

    public void setToolbar(Toolbar toolbar){
        DebugUtil.d(TAG,"setToolbar");
        mToolbar = toolbar;
        if(mTodayWeatherView != null){
            mTodayWeatherView.setToolbar(toolbar);
        }
    }

    public void setSelectedPosition(int position){
        mSelectedPosition = position;
        mLeftMenuAdapter.notifyDataSetChanged();
    }

    class LeftMenuAdapter extends RecyclerView.Adapter<LeftMenuViewHolder>{

        @NonNull
        @Override
        public LeftMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.item_left_menu_view,parent,false);
            return new LeftMenuViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LeftMenuViewHolder holder, final int position) {
            holder.mItemLeftMenuTitleTv.setText(MENU_TITLE_ARRAY[position]);
            if(position == mSelectedPosition){
                holder.mItemLeftMenuContentLl.setSelected(true);
            }else{
                holder.mItemLeftMenuContentLl.setSelected(false);
            }

            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedPosition(position);
                    EventBusUtils.post(new SelectLeftMenuEvent(position));
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return MENU_TITLE_ARRAY.length;
        }
    }

    class LeftMenuViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public LinearLayout mItemLeftMenuContentLl;
        public TextView mItemLeftMenuTitleTv;

        public LeftMenuViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemLeftMenuContentLl = mItemView.findViewById(R.id.item_left_menu_content_ll);
            mItemLeftMenuTitleTv = mItemView.findViewById(R.id.item_left_menu_title_tv);
        }
    }
}
