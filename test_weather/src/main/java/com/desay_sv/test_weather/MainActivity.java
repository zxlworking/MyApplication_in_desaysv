package com.desay_sv.test_weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.desay_sv.test_weather.event.LocatePermissionSuccessEvent;
import com.desay_sv.test_weather.event.RequestLocatePermissionEvent;
import com.desay_sv.test_weather.event.SelectLeftMenuEvent;
import com.desay_sv.test_weather.fragment.AccountFragment;
import com.desay_sv.test_weather.fragment.LeftMenuFragment;
import com.desay_sv.test_weather.fragment.QSBKFragment;
import com.desay_sv.test_weather.fragment.TaoBaoAnchorFragment;
import com.desay_sv.test_weather.utils.CommonUtils;
import com.desay_sv.test_weather.utils.Constants;
import com.desay_sv.test_weather.utils.EventBusUtils;
import com.zxl.common.DebugUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Context mContext;

    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mLeftMenuView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private LeftMenuFragment mLeftMenuFragment;
//    private QSBKFragment mQSBKFragment;
//    private TaoBaoAnchorFragment mTaoBaoAnchorFragment;
//    private AccountFragment mAccountFragment;

    private List<Fragment> mContentFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DebugUtil.d(TAG,"onCreate");

        mContext = this;
        EventBusUtils.register(this);

        mToolbar = findViewById(R.id.custom_tool_bar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLeftMenuView = findViewById(R.id.left_menu_view);

        mToolbar.setTitle("Toolbar");//设置Toolbar标题
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff")); //设置标题颜色
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

        showLeftMenuFragment();

        initContentFragments();

        showContentFragment(Constants.LEFT_MENU_POSITION_0);

        mLeftMenuFragment.setToolbar(mToolbar);

    }

    private void showLeftMenuFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(null == mLeftMenuFragment){
            mLeftMenuFragment = (LeftMenuFragment) Fragment.instantiate(mContext,"com.desay_sv.test_weather.fragment.LeftMenuFragment");
            fragmentTransaction.add(R.id.left_menu_view,mLeftMenuFragment);
        }else{
            fragmentTransaction.show(mLeftMenuFragment);
        }
        fragmentTransaction.commit();
    }

    private void initContentFragments(){
        mContentFragments.clear();
        QSBKFragment mQSBKFragment = (QSBKFragment) Fragment.instantiate(mContext,"com.desay_sv.test_weather.fragment.QSBKFragment");
        mContentFragments.add(mQSBKFragment);
        TaoBaoAnchorFragment mTaoBaoAnchorFragment = (TaoBaoAnchorFragment) Fragment.instantiate(mContext,"com.desay_sv.test_weather.fragment.TaoBaoAnchorFragment");
        mContentFragments.add(mTaoBaoAnchorFragment);
        AccountFragment mAccountFragment = (AccountFragment) Fragment.instantiate(mContext,"com.desay_sv.test_weather.fragment.AccountFragment");
        mContentFragments.add(mAccountFragment);
    }

    private void showContentFragment(int index){

        for(int i = 0; i < mContentFragments.size(); i++){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = mContentFragments.get(i);
            DebugUtil.d(TAG,"showContentFragment::i = " + i);
            if(i == index){
                DebugUtil.d(TAG,"showContentFragment::index = " + index + "::isAdded = " + fragment.isAdded());
                DebugUtil.d(TAG,"showContentFragment::fragment = " + fragment);
                if(fragment.isAdded()){
                    fragmentTransaction.show(fragment);
                }else{
                    fragmentTransaction.add(R.id.container_view,fragment);
                    fragmentTransaction.show(fragment);
                }
                fragmentTransaction.commit();
            }
        }

        for(int i = 0; i < mContentFragments.size(); i++){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = mContentFragments.get(i);
            if(i != index){
                fragmentTransaction.hide(fragment);
            }
            fragmentTransaction.commit();
        }
    }

    private void requestLocatePermission() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                || PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionOk = true;
        if (requestCode == 1) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isPermissionOk = false;
                    break;
                }
            }
        }
        if(isPermissionOk){
            EventBusUtils.post(new LocatePermissionSuccessEvent());
        }
        DebugUtil.d(TAG,"onRequestPermissionsResult::isPermissionOk = " + isPermissionOk);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unregister(this);

        mDrawerLayout.removeDrawerListener(mActionBarDrawerToggle);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestDoLocateEvent(RequestLocatePermissionEvent event){
        requestLocatePermission();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectLeftMenuEvent(SelectLeftMenuEvent event){
        showContentFragment(event.mPosition);
        mDrawerLayout.closeDrawer(mLeftMenuView,true);
    }

}
