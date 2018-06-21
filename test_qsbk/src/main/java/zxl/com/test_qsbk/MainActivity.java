package zxl.com.test_qsbk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    private IQueryQSBK mIQueryQSBK;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private RecyclerView mRecyclerView;
    private CalculateAdapter mCalculateAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

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

                    List<QSBKElement> mTemp = (List<QSBKElement>) msg.obj;
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
                loadData(true,1);
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


        new Thread(new Runnable() {
            @Override
            public void run() {
//                Call<ResponseBody> mCall = mIQueryQSBK.queryQSBK(page);
//                mCall.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            String result = new String(response.body().bytes());
//                            System.out.println("zxl--->onResponse--->"+ result);
//                            Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//                            QSBKElementList mQSBKElementList = mGson.fromJson(result,QSBKElementList.class);
//                            System.out.println("zxl--->onResponse--->mQSBKElementList--->"+ mQSBKElementList);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        System.out.println("zxl--->onFailure--->"+t.toString());
//                    }
//                });
                Call<QSBKElementList> mCall = mIQueryQSBK.queryQSBK(page);
                mCall.enqueue(new Callback<QSBKElementList>() {
                    @Override
                    public void onResponse(Call<QSBKElementList> call, Response<QSBKElementList> response) {
                        QSBKElementList mQSBKElementList = response.body();
                        System.out.println("zxl--->onResponse--->"+ mQSBKElementList);

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
                    public void onFailure(Call<QSBKElementList> call, Throwable t) {
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
                    View mItemView = LayoutInflater.from(mContext).inflate(R.layout.item_view, parent, false);
                    return new QSBKViewHolder(mItemView);
                case FOOT_TYPE:
                    View mItemFootView = LayoutInflater.from(mContext).inflate(R.layout.item_foot_view, parent, false);
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

                        View mBtnErrorRefresh = mQSBKFootViewHolder.mLoadErrorView.findViewById(R.id.btn_error_refresh);

                        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mCalculateAdapter.setLoadDataState(CalculateAdapter.LOADING_DATA_STATE);
                                loadData(false, mCurrentPage + 1);
                            }
                        });
                        break;
                }
            }else{
                if(mQSBKViewHolder != null){
                    final QSBKElement mQSBKElement = mQSBKElements.get(position);
                    Uri mAuthorHeadImgUri = Uri.parse(mQSBKElement.author_head_img);
                    mQSBKViewHolder.mAuthorHeadImg.setImageURI(mAuthorHeadImgUri);
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
                        mQSBKViewHolder.mThumbImg.setImageURI(mQSBKElement.thumb);

                        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics outMetrics = new DisplayMetrics();
                        wm.getDefaultDisplay().getMetrics(outMetrics);
                        setControllerListener(mQSBKViewHolder.mThumbImg, mQSBKElement.thumb, (int) (outMetrics.widthPixels - 24 * outMetrics.density));
                    }else{
                        mQSBKViewHolder.mThumbImg.setVisibility(View.GONE);
                    }
                    mQSBKViewHolder.mVoteNumberTv.setText(String.valueOf(mQSBKElement.vote_number));
                    mQSBKViewHolder.mCommentNumberTv.setText(String.valueOf(mQSBKElement.comment_number));

                    mQSBKViewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            String mQSBKElementStr = mGson.toJson(mQSBKElement);
                            Intent mIntent = new Intent(mContext, QSBKDetailActivity.class);
                            mIntent.putExtra(QSBKDetailActivity.EXTRA_QSBK_ELEMENT, mQSBKElementStr);
                            startActivity(mIntent);
                        }
                    });
                }
            }
        }

        public void setControllerListener(final SimpleDraweeView simpleDraweeView, String imagePath, final int imageWidth) {
            final ViewGroup.LayoutParams layoutParams = simpleDraweeView.getLayoutParams();
            ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
                    if (imageInfo == null) {
                        return;
                    }
                    int height = imageInfo.getHeight();
                    int width = imageInfo.getWidth();
                    layoutParams.width = imageWidth;
                    layoutParams.height = (int) ((float) (imageWidth * height) / (float) width);
                    simpleDraweeView.setLayoutParams(layoutParams);
                }

                @Override
                public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
                    Log.d("TAG", "Intermediate image received");
                }

                @Override
                public void onFailure(String id, Throwable throwable) {
                    throwable.printStackTrace();
                }
            };
            DraweeController controller = Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener).setUri(Uri.parse(imagePath)).build();
            simpleDraweeView.setController(controller);
        }

        @Override
        public int getItemCount() {
            return mQSBKElements.size() + (mCurrentPage < mTotalPage - 1 ? 1 : 0);
        }
    }

    public class QSBKViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public SimpleDraweeView mAuthorHeadImg;
        public SimpleDraweeView mThumbImg;
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
