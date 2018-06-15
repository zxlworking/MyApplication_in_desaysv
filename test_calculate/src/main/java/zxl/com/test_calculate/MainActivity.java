package zxl.com.test_calculate;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private static final int MSG_FIRST_LOAD_START = 1;
    private static final int MSG_FIRST_LOAD_SUCCESS = 2;
    private static final int MSG_FIRST_LOAD_ERROR = 3;
    private static final int MSG_LOAD_START = 4;
    private static final int MSG_LOAD_SUCCESS = 5;
    private static final int MSG_LOAD_ERROR = 6;

    private Context mContext;

    private Retrofit mRetrofit;
    private IQueryCalculate mIQueryCalculate;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private RecyclerView mRecyclerView;
    private CalculateAdapter mCalculateAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private int mCurrentPage = 0;
    private int mTotalPage = 0;
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

                    List<CalculateElement> mFirstTemp = (List<CalculateElement>) msg.obj;
                    mCalculateAdapter.setData(mFirstTemp);

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

                    List<CalculateElement> mTemp = (List<CalculateElement>) msg.obj;
                    mCalculateAdapter.addData(mTemp);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case MSG_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    mCalculateAdapter.setLoadDataState(CalculateAdapter.LOAD_DATA_ERROR_STATE);

                    isLoading = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mLoadingView = findViewById(R.id.loading_view);
        mLoadErrorView = findViewById(R.id.load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.btn_error_refresh);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(true,0, mPageCount);
            }
        });

        mRecyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCalculateAdapter = new CalculateAdapter();
        mRecyclerView.setAdapter(mCalculateAdapter);

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#3F51B5"),Color.parseColor("#303F9F"),Color.parseColor("#FF4081"));
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                loadData(true,0, mPageCount);
            }
        });

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://www.zxltest.cn/")
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mIQueryCalculate = mRetrofit.create(IQueryCalculate.class);

        loadData(true, 0, mPageCount);
    }

    public void loadData(final boolean isFirstLoad, final int page, final int count){
        if(isLoading){
            return;
        }
        isLoading = true;

        if(isFirstLoad){
            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_START);
        }else{
            mHandler.sendEmptyMessage(MSG_LOAD_START);
        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                Call<CalculateElementList> mCall = mIQueryCalculate.queryCalculate(page,count);
                mCall.enqueue(new Callback<CalculateElementList>() {
                    @Override
                    public void onResponse(Call<CalculateElementList> call, Response<CalculateElementList> response) {
                        CalculateElementList mCalculateElementList = response.body();
                        System.out.println("zxl--->onResponse--->"+ mCalculateElementList);

                        mCurrentPage = mCalculateElementList.current_page;
                        mTotalPage = mCalculateElementList.total_page;
                        mPageCount = mCalculateElementList.page_count;
                        if(isFirstLoad){
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_FIRST_LOAD_SUCCESS;
                            message.obj = mCalculateElementList.result;
                            message.sendToTarget();
                        }else{
                            Message message = mHandler.obtainMessage();
                            message.what = MSG_LOAD_SUCCESS;
                            message.obj = mCalculateElementList.result;
                            message.sendToTarget();
                        }
                    }

                    @Override
                    public void onFailure(Call<CalculateElementList> call, Throwable t) {
                        System.out.println("zxl--->onFailure--->"+t.toString());
                        if(isFirstLoad){
                            mHandler.sendEmptyMessage(MSG_FIRST_LOAD_ERROR);
                        }else{
                            mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                        }
                    }
                });

            }
        }).start();
    }

    public class CalculateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public static final int LOADING_DATA_STATE = 1;
        public static final int LOAD_DATA_SUCCESS_STATE =2 ;
        public static final int LOAD_DATA_ERROR_STATE = 3;

        private static final int CONTENT_TYPE = 1;
        private static final int FOOT_TYPE = 2;

        private int mCurrentState = LOAD_DATA_SUCCESS_STATE;
        private List<CalculateElement> mCalculateElements = new ArrayList<>();

        public void setData(List<CalculateElement> elements){
            mCalculateElements.clear();
            mCalculateElements.addAll(elements);
            mCurrentState = LOAD_DATA_SUCCESS_STATE;
            notifyDataSetChanged();
        }

        public void addData(List<CalculateElement> elements){
            mCalculateElements.addAll(elements);
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
                    View mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
                    return new CalculateViewHolder(mItemView);
                case FOOT_TYPE:
                    View mItemFootView = LayoutInflater.from(mContext).inflate(R.layout.item_foot_view, parent, false);
                    return new CalculateFootViewHolder(mItemFootView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CalculateViewHolder mCalculateViewHolder = null;
            if(holder instanceof CalculateViewHolder){
                mCalculateViewHolder = (CalculateViewHolder) holder;
            }
            CalculateFootViewHolder mCalculateFootViewHolder = null;
            if(holder instanceof CalculateFootViewHolder){
                mCalculateFootViewHolder = (CalculateFootViewHolder) holder;
            }
            if(position == getItemCount() - 1 && mCurrentPage < mTotalPage - 1 && mCalculateFootViewHolder != null){
                switch (mCurrentState){
                    case LOAD_DATA_SUCCESS_STATE:
                        mCalculateFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mCalculateFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);

                        loadData(false,mCurrentPage + 1, mPageCount);
                        break;
                    case LOADING_DATA_STATE:
                        mCalculateFootViewHolder.mLoadErrorView.setVisibility(View.GONE);
                        mCalculateFootViewHolder.mLoadingView.setVisibility(View.VISIBLE);
                        break;
                    case LOAD_DATA_ERROR_STATE:
                        mCalculateFootViewHolder.mLoadErrorView.setVisibility(View.VISIBLE);
                        mCalculateFootViewHolder.mLoadingView.setVisibility(View.GONE);

                        View mBtnErrorRefresh = mCalculateFootViewHolder.mLoadErrorView.findViewById(R.id.btn_error_refresh);

                        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCalculateAdapter.setLoadDataState(CalculateAdapter.LOADING_DATA_STATE);
                                loadData(false, mCurrentPage + 1, mPageCount);
                            }
                        });
                        break;
                }
            }else{
                if(mCalculateViewHolder != null){
                    CalculateElement mCalculateElement = mCalculateElements.get(position);
                    mCalculateViewHolder.mCalculateIdTv.setText(""+mCalculateElement._id);
                    mCalculateViewHolder.mCalculateArg1Tv.setText(""+mCalculateElement.calculate_arg1);
                    mCalculateViewHolder.mCalculateOpTv.setText(mCalculateElement.calculate_operator_str);
                    mCalculateViewHolder.mCalculateArg2Tv.setText(""+mCalculateElement.calculate_arg2);
                    if(mCalculateElement.is_input_calculate_result_right == 1){
                        mCalculateViewHolder.mCalculateResultTv.setText(mCalculateElement.calculate_result);
                    }else{
                        SpannableString spannableString = new SpannableString(mCalculateElement.calculate_result+"/"+mCalculateElement.input_calculate_result);
                        spannableString.setSpan(new BackgroundColorSpan(Color.RED), mCalculateElement.calculate_result.length() + 1,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), mCalculateElement.calculate_result.length() + 1,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mCalculateViewHolder.mCalculateResultTv.setText(spannableString);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return mCalculateElements.size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
        }
    }

    public class CalculateViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;
        public TextView mCalculateIdTv;
        public TextView mCalculateArg1Tv;
        public TextView mCalculateOpTv;
        public TextView mCalculateArg2Tv;
        public TextView mCalculateResultTv;
        public TextView mInputCalculateResultTv;

        public CalculateViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mCalculateIdTv = mItemView.findViewById(R.id.calculate_id_tv);
            mCalculateArg1Tv = mItemView.findViewById(R.id.calculate_arg1_tv);
            mCalculateOpTv = mItemView.findViewById(R.id.calculate_op_tv);
            mCalculateArg2Tv = mItemView.findViewById(R.id.calculate_arg2_tv);
            mCalculateResultTv = mItemView.findViewById(R.id.calculate_result_tv);
            mInputCalculateResultTv = mItemView.findViewById(R.id.input_calculate_result_tv);
        }
    }

    public class CalculateFootViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        private View mLoadingView;
        private View mLoadErrorView;

        public CalculateFootViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLoadingView = mItemView.findViewById(R.id.loading_view);
            mLoadErrorView = mItemView.findViewById(R.id.load_error_view);
        }
    }

    public interface IQueryCalculate{
        @GET("/cgi_server/cgi/server_query_calculate.py")
        public Call<CalculateElementList> queryCalculate(@Query("page") int page, @Query("count") int count);
    }
}
