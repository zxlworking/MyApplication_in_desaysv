package zxl.com.test_qsbk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by uidq0955 on 2018/6/15.
 */

public class QSBKDetailActivity extends Activity {

    private static final int MSG_LOAD_START = 1;
    private static final int MSG_LOAD_SUCCESS = 2;
    private static final int MSG_LOAD_ERROR = 3;

    public static final String EXTRA_QSBK_ELEMENT = "EXTRA_QSBK_ELEMENT";

    private Context mContext;

    private IQueryQSBKDetail mIQueryQSBKDetail;
    private Retrofit mRetrofit;

    private View mLoadingView;
    private View mLoadErrorView;
    private Button mBtnErrorRefresh;

    private RecyclerView mRecyclerView;
    private QSBKDetailAdapter mQSBKDetailAdapter;

    private QSBKElement mQSBKElement;

    private boolean isLoading = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOAD_START:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    mLoadErrorView.setVisibility(View.GONE);
                    break;
                case MSG_LOAD_SUCCESS:
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.GONE);

                    QSBKDetail mTemp = (QSBKDetail) msg.obj;
                    mQSBKDetailAdapter.setData(mQSBKElement,mTemp);

                    isLoading = false;
                    break;
                case MSG_LOAD_ERROR:
                    mRecyclerView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.GONE);
                    mLoadErrorView.setVisibility(View.VISIBLE);


                    isLoading = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qsbk_detail_view);

        mContext = this;

        mLoadingView = findViewById(R.id.loading_view);
        mLoadErrorView = findViewById(R.id.load_error_view);
        mBtnErrorRefresh = mLoadErrorView.findViewById(R.id.btn_error_refresh);

        mBtnErrorRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(mQSBKElement);
            }
        });


        mRecyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mQSBKDetailAdapter = new QSBKDetailAdapter();
        mRecyclerView.setAdapter(mQSBKDetailAdapter);

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://www.zxltest.cn/")
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mIQueryQSBKDetail = mRetrofit.create(IQueryQSBKDetail.class);

        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        mQSBKElement = mGson.fromJson(getIntent().getStringExtra(EXTRA_QSBK_ELEMENT),QSBKElement.class);
        loadData(mQSBKElement);
    }

    private void loadData(final QSBKElement mQSBKElement) {
        if(isLoading){
            return;
        }
        isLoading = true;

        mHandler.sendEmptyMessage(MSG_LOAD_START);

        System.out.println("zxl--->QSBKDetailActivity loadData--->"+ mQSBKElement);

        new Thread(new Runnable() {
            @Override
            public void run() {
//                Call<ResponseBody> mCall = mIQueryQSBKDetail.queryQSBKDetail(mQSBKElement.author_id);
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
                Call<QSBKDetail> mCall = mIQueryQSBKDetail.queryQSBKDetail(mQSBKElement.author_id);
//                Call<QSBKDetail> mCall = mIQueryQSBKDetail.queryQSBKDetail("120535270");
                mCall.enqueue(new Callback<QSBKDetail>() {
                    @Override
                    public void onResponse(Call<QSBKDetail> call, Response<QSBKDetail> response) {
                        QSBKDetail mQSBKDetail = response.body();
                        System.out.println("zxl--->mQSBKDetail--->"+ mQSBKDetail);

                        Message message = mHandler.obtainMessage();
                        message.what = MSG_LOAD_SUCCESS;
                        message.obj = mQSBKDetail;
                        message.sendToTarget();
                    }

                    @Override
                    public void onFailure(Call<QSBKDetail> call, Throwable t) {
                        System.out.println("zxl--->onFailure--->"+t.toString());
                        mHandler.sendEmptyMessage(MSG_LOAD_ERROR);
                    }
                });
            }
        }).start();
    }

    public class QSBKDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int HEAD_TYPE = 1;
        private static final int CONTENT_TYPE = 2;

        private QSBKElement mQSBKElement = null;
        private QSBKDetail mQsbkDetail = null;

        public void setData(QSBKElement qSBKElement, QSBKDetail qsbkDetail){
            mQSBKElement = qSBKElement;
            mQsbkDetail = qsbkDetail;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0){
                return HEAD_TYPE;
            }
            return CONTENT_TYPE;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType){
                case HEAD_TYPE:
                    View mItemHeadView = LayoutInflater.from(mContext).inflate(R.layout.item_qsbk_detail_head_view, parent, false);
                    return new QSBKDetailHeadViewHolder(mItemHeadView);
                case CONTENT_TYPE:
                    View mItemCommentView = LayoutInflater.from(mContext).inflate(R.layout.item_qsbk_detail_comment_view, parent, false);
                    return new QSBKDetailCommentViewHolder(mItemCommentView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            QSBKDetailHeadViewHolder mQSBKDetailHeadViewHolder = null;
            if(holder instanceof QSBKDetailHeadViewHolder){
                mQSBKDetailHeadViewHolder = (QSBKDetailHeadViewHolder) holder;
            }
            QSBKDetailCommentViewHolder mQSBKDetailCommentViewHolder = null;
            if(holder instanceof QSBKDetailCommentViewHolder){
                mQSBKDetailCommentViewHolder = (QSBKDetailCommentViewHolder) holder;
            }
            if(position == 0){
                if(mQSBKDetailHeadViewHolder != null){
                    mQSBKDetailHeadViewHolder.mAuthorHeadImg.setImageURI(mQSBKElement.author_head_img);
                    mQSBKDetailHeadViewHolder.mAuthorNameTv.setText(mQSBKElement.author_name);
                    if(mQSBKElement.isAnonymity()){
                        mQSBKDetailHeadViewHolder.mAuthorSexAgeLl.setVisibility(View.GONE);
                    }else{
                        mQSBKDetailHeadViewHolder.mAuthorSexAgeLl.setVisibility(View.VISIBLE);
                        if(mQSBKElement.author_sex == QSBKElement.SEX_MAN){
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setText("男");
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#0000ff"));
                            mQSBKDetailHeadViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#0000ff"));
                        }else if(mQSBKElement.author_sex == QSBKElement.SEX_FEMALE){
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setText("女");
                            mQSBKDetailHeadViewHolder.mAuthorSexTv.setTextColor(Color.parseColor("#aa00aa"));
                            mQSBKDetailHeadViewHolder.mAuthorAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                        }
                        mQSBKDetailHeadViewHolder.mAuthorAgeTv.setText(mQSBKElement.author_age+"岁");
                    }
                    mQSBKDetailHeadViewHolder.mContentTv.setText(mQsbkDetail.qsbk_detail_content);
                    if(mQSBKElement.hasThumb()){
                        mQSBKDetailHeadViewHolder.mThumbImg.setVisibility(View.VISIBLE);
                        mQSBKDetailHeadViewHolder.mThumbImg.setImageURI(mQSBKElement.thumb);

                        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics outMetrics = new DisplayMetrics();
                        wm.getDefaultDisplay().getMetrics(outMetrics);
                        setControllerListener(mQSBKDetailHeadViewHolder.mThumbImg, mQSBKElement.thumb, (int) (outMetrics.widthPixels - 24 * outMetrics.density));
                    }else{
                        mQSBKDetailHeadViewHolder.mThumbImg.setVisibility(View.GONE);
                    }
                    mQSBKDetailHeadViewHolder.mCommentCount.setText("评论("+mQsbkDetail.user_comment_list.size()+")：");
                }
            }else{
                QSBKComment mQsbkComment = mQsbkDetail.user_comment_list.get(position - 1);

                mQSBKDetailCommentViewHolder.mUserHeadImg.setImageURI(mQsbkComment.user_head_img);
                mQSBKDetailCommentViewHolder.mUserNameTv.setText(mQsbkComment.user_name);

                if(mQsbkComment.user_sex == QSBKElement.SEX_MAN){
                    mQSBKDetailCommentViewHolder.mUserSexTv.setText("男");
                    mQSBKDetailCommentViewHolder.mUserSexTv.setTextColor(Color.parseColor("#0000ff"));
                    mQSBKDetailCommentViewHolder.mUserAgeTv.setTextColor(Color.parseColor("#0000ff"));
                }else if(mQsbkComment.user_sex == QSBKElement.SEX_FEMALE){
                    mQSBKDetailCommentViewHolder.mUserSexTv.setText("女");
                    mQSBKDetailCommentViewHolder.mUserSexTv.setTextColor(Color.parseColor("#aa00aa"));
                    mQSBKDetailCommentViewHolder.mUserAgeTv.setTextColor(Color.parseColor("#aa00aa"));
                }
                mQSBKDetailCommentViewHolder.mUserAgeTv.setText(mQsbkComment.user_age+"岁");
                mQSBKDetailCommentViewHolder.mCommentTv.setText(mQsbkComment.comment_content);
                mQSBKDetailCommentViewHolder.mCommentReport.setText(""+mQsbkComment.comment_report);
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
            return mQsbkDetail.user_comment_list.size() + 1;
        }
    }

    public class QSBKDetailHeadViewHolder extends RecyclerView.ViewHolder{

        public View mItemView;
        public SimpleDraweeView mAuthorHeadImg;
        public SimpleDraweeView mThumbImg;
        public TextView mAuthorNameTv;
        public TextView mAuthorSexTv;
        public TextView mAuthorAgeTv;
        public TextView mContentTv;
        public TextView mCommentCount;

        public LinearLayout mAuthorSexAgeLl;

        public QSBKDetailHeadViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;

            mAuthorHeadImg = mItemView.findViewById(R.id.author_head_img);
            mThumbImg = mItemView.findViewById(R.id.thumb_img);
            mAuthorNameTv = mItemView.findViewById(R.id.author_name_tv);
            mAuthorSexTv = mItemView.findViewById(R.id.author_sex_tv);
            mAuthorAgeTv = mItemView.findViewById(R.id.author_age_tv);
            mContentTv = mItemView.findViewById(R.id.content_tv);
            mCommentCount = mItemView.findViewById(R.id.comment_count);
            mAuthorSexAgeLl = mItemView.findViewById(R.id.author_sex_age_ll);
        }
    }

    public class QSBKDetailCommentViewHolder extends RecyclerView.ViewHolder{

        private View mItemView;

        public SimpleDraweeView mUserHeadImg;
        public TextView mUserNameTv;
        public TextView mUserSexTv;
        public TextView mUserAgeTv;
        public TextView mCommentTv;
        public TextView mCommentReport;

        public LinearLayout mUserSexAgeLl;

        public QSBKDetailCommentViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mUserHeadImg = mItemView.findViewById(R.id.user_head_img);
            mUserNameTv = mItemView.findViewById(R.id.user_name_tv);
            mUserSexTv = mItemView.findViewById(R.id.user_sex_tv);
            mUserAgeTv = mItemView.findViewById(R.id.user_age_tv);
            mCommentTv = mItemView.findViewById(R.id.comment_content_tv);
            mCommentReport = mItemView.findViewById(R.id.comment_report);
            mUserSexAgeLl = mItemView.findViewById(R.id.user_sex_age_ll);
        }
    }

    public interface IQueryQSBKDetail{
        @GET("/cgi_server/cgi_qsbk/cgi_qsbk_detail.py")
        public Call<QSBKDetail> queryQSBKDetail(@Query("author_id") String author_id);
    }
}
