package com.desay_sv.test_weather.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.desay_sv.test_weather.QSBKActivity;
import com.desay_sv.test_weather.QSBKDetailActivity;
import com.desay_sv.test_weather.R;
import com.desay_sv.test_weather.http.HttpUtils;
import com.desay_sv.test_weather.http.data.QSBKElement;
import com.desay_sv.test_weather.http.data.QSBKElementList;
import com.desay_sv.test_weather.http.data.ResponseBaseBean;
import com.desay_sv.test_weather.http.listener.NetRequestListener;
import com.desay_sv.test_weather.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zxl on 2018/9/20.
 */

public class QSBKFragment extends BaseFragment {
    private static final String TAG = "QSBKFragment";

    private static final int MSG_FIRST_LOAD_START = 1;
    private static final int MSG_FIRST_LOAD_SUCCESS = 2;
    private static final int MSG_FIRST_LOAD_ERROR = 3;
    private static final int MSG_LOAD_START = 4;
    private static final int MSG_LOAD_SUCCESS = 5;
    private static final int MSG_LOAD_ERROR = 6;

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private RecyclerView mRecyclerView;
    private QSBKAdapter mQSBKAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Retrofit mRetrofit;
    private IQueryQSBK mIQueryQSBK;

    private int mCurrentPage = 1;
    private int mTotalPage = Integer.MAX_VALUE;
    private int mPageCount = 10;

    private boolean isLoading = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_FIRST_LOAD_START:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    mLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_FIRST_LOAD_SUCCESS:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    List<QSBKElement> mFirstTemp = (List<QSBKElement>) msg.obj;
                    mQSBKAdapter.setData(mFirstTemp);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_FIRST_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.VISIBLE);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_START:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_LOAD_SUCCESS:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    List<QSBKElement> mTemp = (List<QSBKElement>) msg.obj;
                    mQSBKAdapter.addData(mTemp);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    mQSBKAdapter.setLoadDataState(QSBKActivity.CalculateAdapter.LOAD_DATA_ERROR_STATE);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.fragment_qsbk,null);

        mLoadingView = mContentView.findViewById(R.id.qsbk_loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.qsbk_load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.load_error_btn);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(true,1);
            }
        });

        mRecyclerView = mContentView.findViewById(R.id.recycler_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mQSBKAdapter = new QSBKAdapter();
        mRecyclerView.setAdapter(mQSBKAdapter);

        mSwipeRefreshLayout = mContentView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData(true,1);
            }
        });

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
        mRetrofit = new Retrofit.Builder()
                //.baseUrl("http://www.zxltest.cn/")
                .baseUrl("http://118.25.178.69/")
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mIQueryQSBK = mRetrofit.create(IQueryQSBK.class);

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData(true, 1);
    }

    public void loadData(final boolean isFirstLoad, final int page){
        if(isLoading){
            return;
        }
        isLoading = true;

        if(isFirstLoad){
            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_START);
        }else{
            mHandler.sendEmptyMessage(MSG_LOAD_START);
        }

        HttpUtils.getInstance().getQSBK(mActivity, page, new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                QSBKElementList mQSBKElementList = (QSBKElementList) responseBaseBean;

                mCurrentPage = mQSBKElementList.current_page;
                if(isFirstLoad){
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_FIRST_LOAD_SUCCESS;
                    message.obj = mQSBKElementList.result;
                    message.sendToTarget();
                }else{
                    Message message = mHandler.obtainMessage();
                    message.what = MSG_LOAD_SUCCESS;
                    message.obj = mQSBKElementList.result;
                    message.sendToTarget();
                }
            }

            @Override
            public void onNetError() {
                if(isFirstLoad){
                    mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                }else{
                    mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                }
            }

            @Override
            public void onNetError(Throwable e) {
                if(isFirstLoad){
                    mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                }else{
                    mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                }
            }

            @Override
            public void onServer(ResponseBaseBean responseBaseBean) {
                if(isFirstLoad){
                    mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                }else{
                    mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                }
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Call<QSBKElementList> mCall = mIQueryQSBK.queryQSBK(page);
//                mCall.enqueue(new Callback<QSBKElementList>() {
//                    @Override
//                    public void onResponse(Call<QSBKElementList> call, Response<QSBKElementList> response) {
//                        QSBKElementList mQSBKElementList = response.body();
//                        System.out.println("zxl--->onResponse--->"+ mQSBKElementList);
//
//                        mCurrentPage = mQSBKElementList.current_page;
//                        if(isFirstLoad){
//                            Message message = mHandler.obtainMessage();
//                            message.what = MSG_FIRST_LOAD_SUCCESS;
//                            message.obj = mQSBKElementList.result;
//                            message.sendToTarget();
//                        }else{
//                            Message message = mHandler.obtainMessage();
//                            message.what = MSG_LOAD_SUCCESS;
//                            message.obj = mQSBKElementList.result;
//                            message.sendToTarget();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<QSBKElementList> call, Throwable t) {
//                        System.out.println("zxl--->onFailure--->"+t.toString());
//                        if(isFirstLoad){
//                            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
//                        }else{
//                            mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
//                        }
//                    }
//                });
//
//            }
//        }).start();
    }

    public class QSBKAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public static final int LOADING_DATA_STATE = 1;
        public static final int LOAD_DATA_SUCCESS_STATE =2 ;
        public static final int LOAD_DATA_ERROR_STATE = 3;

        private static final int CONTENT_TYPE = 1;
        private static final int FOOT_TYPE = 2;

        private int mCurrentState = LOAD_DATA_SUCCESS_STATE;
        private List<QSBKElement> mQSBKElements = new ArrayList<>();

        public void setData(List<QSBKElement> elements){
            mQSBKElements.clear();
            mQSBKElements.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyDataSetChanged();
        }

        public void addData(List<QSBKElement> elements){
            mQSBKElements.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyDataSetChanged();
        }

        public void setLoadDataState(int state){
            mCurrentState = state;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1){
                return FOOT_TYPE;
            }
            return CONTENT_TYPE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case CONTENT_TYPE:
                    View mItemView = LayoutInflater.from(mActivity).inflate(R.layout.item_qsbk_view, parent, false);
                    return new QSBKViewHolder(mItemView);
                case FOOT_TYPE:
                    View mItemFootView = LayoutInflater.from(mActivity).inflate(R.layout.item_qsbk_foot_view, parent, false);
                    return new QSBKFootViewHolder(mItemFootView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            QSBKViewHolder mQSBKViewHolder = null;
            if(holder instanceof QSBKViewHolder){
                mQSBKViewHolder = (QSBKViewHolder) holder;
            }
            QSBKFootViewHolder mQSBKFootViewHolder = null;
            if(holder instanceof QSBKFootViewHolder){
                mQSBKFootViewHolder = (QSBKFootViewHolder) holder;
            }
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1 && mQSBKFootViewHolder != null){
                switch (mCurrentState){
                    case LOAD_DATA_SUCCESS_STATE:
                        mQSBKFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mQSBKFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);

                        loadData(false,mCurrentPage + 1);
                        break;
                    case LOADING_DATA_STATE:
                        mQSBKFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mQSBKFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
                        break;
                    case LOAD_DATA_ERROR_STATE:
                        mQSBKFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
                        mQSBKFootViewHolder.mLoadingView.setVisibility(View.GONE);

                        View mBtnErrorRefresh = mQSBKFootViewHolder.mLoadErrorView.findViewById(R.id.load_error_btn);

                        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mQSBKAdapter.setLoadDataState(QSBKAdapter.LOADING_DATA_STATE);
                                loadData(false, mCurrentPage + 1);
                            }
                        });
                        break;
                }
            }else{
                if(mQSBKViewHolder != null){
                    final QSBKElement mQSBKElement = mQSBKElements.get(position);
                    Glide.with(mActivity).load(mQSBKElement.author_head_img).into(mQSBKViewHolder.mAuthorHeadImg);
                    mQSBKViewHolder.mAuthorNameTv.setText(mQSBKElement.author_name);
                    if(mQSBKElement.isAnonymity()){
                        mQSBKViewHolder.mAuthorSexAgeLl.setVisibility(View.GONE);
                    }else{
                        mQSBKViewHolder.mAuthorSexAgeLl.setVisibility(View.VISIBLE);
                        if(mQSBKElement.author_sex == QSBKElement.SEX_MAN){
                            mQSBKViewHolder.mAuthorSexTv.setText("男");
                            mQSBKViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#0000ff"));
                            mQSBKViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#0000ff"));
                        }else if(mQSBKElement.author_sex == QSBKElement.SEX_FEMALE){
                            mQSBKViewHolder.mAuthorSexTv.setText("女");
                            mQSBKViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#aa00aa"));
                            mQSBKViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                        }
                        mQSBKViewHolder.mAuthorAgeTv.setText(mQSBKElement.author_age+"岁");
                    }
                    mQSBKViewHolder.mContentTv.setText(mQSBKElement.content);
                    if(mQSBKElement.hasThumb()){
                        mQSBKViewHolder.mThumbImg.setVisibility(View.VISIBLE);
                        Glide.with(mActivity).load(mQSBKElement.thumb).into(mQSBKViewHolder.mThumbImg);

                    }else{
                        mQSBKViewHolder.mThumbImg.setVisibility(View.GONE);
                    }
                    mQSBKViewHolder.mVoteNumberTv.setText(String.valueOf(mQSBKElement.vote_number));
                    mQSBKViewHolder.mCommentNumberTv.setText(String.valueOf(mQSBKElement.comment_number));

                    mQSBKViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String mQSBKElementStr = CommonUtils.mGson.toJson(mQSBKElement);
                            Intent mIntent = new Intent(mActivity, QSBKDetailActivity.class);
                            mIntent.putExtra(QSBKDetailActivity.EXTRA_QSBK_ELEMENT, mQSBKElementStr);
                            startActivity(mIntent);
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return mQSBKElements.size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
        }
    }

    public class QSBKViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public ImageView mAuthorHeadImg;
        public ImageView mThumbImg;
        public TextView mAuthorNameTv;
        public TextView mAuthorSexTv;
        public TextView mAuthorAgeTv;
        public TextView mContentTv;
        public TextView mVoteNumberTv;
        public TextView mCommentNumberTv;

        public LinearLayout mAuthorSexAgeLl;

        public QSBKViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mAuthorHeadImg = mItemView.findViewById(R.id.author_head_img);
            mThumbImg = mItemView.findViewById(R.id.thumb_img);
            mAuthorNameTv = mItemView.findViewById(R.id.author_name_tv);
            mAuthorSexTv = mItemView.findViewById(R.id.author_sex_tv);
            mAuthorAgeTv = mItemView.findViewById(R.id.author_age_tv);
            mContentTv = mItemView.findViewById(R.id.content_tv);
            mVoteNumberTv = mItemView.findViewById(R.id.vote_number_tv);
            mCommentNumberTv = mItemView.findViewById(R.id.comment_number_tv);
            mAuthorSexAgeLl = mItemView.findViewById(R.id.author_sex_age_ll);
        }
    }

    public class QSBKFootViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private View mLoadingView;
        private View mLoadErrorView;

        public QSBKFootViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLoadingView = mItemView.findViewById(R.id.loading_view);
            mLoadErrorView = mItemView.findViewById(R.id.load_error_view);
        }
    }

    public interface IQueryQSBK{
        @GET("/cgi_server/cgi_qsbk/cgi_qsbk.py")
        public Call<QSBKElementList> queryQSBK(@Query("page") int page);
    }
}
