package com.desay_sv.test_weather.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.desay_sv.test_weather.R;
import com.desay_sv.test_weather.http.HttpUtils;
import com.desay_sv.test_weather.http.data.ResponseBaseBean;
import com.desay_sv.test_weather.http.data.UserInfoResponseBean;
import com.desay_sv.test_weather.http.listener.NetRequestListener;
import com.desay_sv.test_weather.utils.CommonUtils;
import com.desay_sv.test_weather.utils.SharePreUtils;
import com.zxl.common.DebugUtil;

/**
 * Created by zxl on 2018/9/21.
 */

public class AccountFragment extends BaseFragment {
    private static final String TAG = "AccountFragment";

    private View mContentView;

    private View mLoadingView;
    private View mLoadErrorView;

    private TextInputLayout mUserNameTextInputLayout;
    private TextInputEditText mUserNameTextInputEditText;
    private TextInputLayout mPassWordTextInputLayout;
    private TextInputEditText mPassWordTextInputEditText;
    private TextInputEditText mPhoneNumberTextInputEditText;
    private TextInputEditText mNickNameTextInputEditText;

    private CardView mRegisterCardView;
    private CardView mLoginCardView;
    private CardView mLogoutCardView;

    private boolean isRegistering = false;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.register_card_view:
                    register();
                    break;
                case R.id.login_card_view:
                    break;
                case R.id.logout_card_view:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DebugUtil.d(TAG,"onCreateView");
        mContentView = inflater.inflate(R.layout.fragment_account,null);

        mLoadingView = mContentView.findViewById(R.id.loading_view);
        mLoadErrorView = mContentView.findViewById(R.id.load_error_view);


        mUserNameTextInputLayout = mContentView.findViewById(R.id.user_name_input_l);
        mUserNameTextInputEditText = mContentView.findViewById(R.id.user_name_input_et);

        mPassWordTextInputLayout = mContentView.findViewById(R.id.pass_word_input_l);
        mPassWordTextInputEditText = mContentView.findViewById(R.id.pass_word_input_et);
        mPassWordTextInputLayout.setPasswordVisibilityToggleEnabled(true);

        mPhoneNumberTextInputEditText = mContentView.findViewById(R.id.phone_number_input_et);
        mNickNameTextInputEditText = mContentView.findViewById(R.id.nick_name_input_et);

        mRegisterCardView = mContentView.findViewById(R.id.register_card_view);
        mLoginCardView = mContentView.findViewById(R.id.login_card_view);
        mLogoutCardView = mContentView.findViewById(R.id.logout_card_view);

        mRegisterCardView.setOnClickListener(mOnClickListener);

        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UserInfoResponseBean userInfoResponseBean = SharePreUtils.getInstance(mActivity).getUserInfo();
        if(userInfoResponseBean != null){
            mRegisterCardView.setVisibility(View.GONE);
            mLoginCardView.setVisibility(View.GONE);
            mLogoutCardView.setVisibility(View.VISIBLE);
        }else{
            mRegisterCardView.setVisibility(View.VISIBLE);
            mLoginCardView.setVisibility(View.VISIBLE);
            mLogoutCardView.setVisibility(View.GONE);
        }
    }

    public UserInfoResponseBean createUserInfo(){
        String userName = mUserNameTextInputEditText.getText().toString();
        String passWord = mPassWordTextInputEditText.getText().toString();
        String phoneNumber = mPhoneNumberTextInputEditText.getText().toString();
        String nickName = mNickNameTextInputEditText.getText().toString();

        if(TextUtils.isEmpty(userName)){
            Toast.makeText(mActivity,"用户名不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }
        if(TextUtils.isEmpty(passWord)){
            Toast.makeText(mActivity,"密码不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }
        if(TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(mActivity,"手机号不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }
        if(TextUtils.isEmpty(nickName)){
            Toast.makeText(mActivity,"昵称不能为空",Toast.LENGTH_SHORT).show();
            return null;
        }

        UserInfoResponseBean userInfoResponseBean = new UserInfoResponseBean();
        userInfoResponseBean.user_name = userName;
        userInfoResponseBean.pass_word = passWord;
        userInfoResponseBean.phone_number = phoneNumber;
        userInfoResponseBean.nick_name = nickName;
        return userInfoResponseBean;
    }

    public void register(){
        UserInfoResponseBean userInfoResponseBean = createUserInfo();
        if(null == userInfoResponseBean){
            return;
        }

        if(isRegistering){
            return;
        }
        isRegistering = true;

        mLoadingView.setVisibility(View.VISIBLE);
        mLoadErrorView.setVisibility(View.GONE);

        HttpUtils.getInstance().register(mActivity, UserInfoResponseBean.USER_OPERATOR_CREATE, CommonUtils.mGson.toJson(userInfoResponseBean), new NetRequestListener() {
            @Override
            public void onSuccess(ResponseBaseBean responseBaseBean) {
                UserInfoResponseBean userInfoResponseBean1 = (UserInfoResponseBean) responseBaseBean;
                mUserNameTextInputEditText.setText("");
                mPassWordTextInputEditText.setText("");
                mPhoneNumberTextInputEditText.setText("");
                mNickNameTextInputEditText.setText("");
                mUserNameTextInputEditText.requestFocus();


                mRegisterCardView.setVisibility(View.VISIBLE);
                mLoginCardView.setVisibility(View.VISIBLE);
                mLogoutCardView.setVisibility(View.GONE);

                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
                if(userInfoResponseBean1.code == 0){
                    Toast.makeText(mActivity,"注册成功!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mActivity,userInfoResponseBean1.desc,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNetError() {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
            }

            @Override
            public void onNetError(Throwable e) {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
            }

            @Override
            public void onServerError(ResponseBaseBean responseBaseBean) {
                mLoadingView.setVisibility(View.GONE);
                mLoadErrorView.setVisibility(View.GONE);
                isRegistering = false;
            }
        });
    }

}
