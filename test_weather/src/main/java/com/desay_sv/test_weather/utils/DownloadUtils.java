package com.desay_sv.test_weather.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.content.FileProvider;

import com.zxl.common.DebugUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by zxl on 2018/10/9.
 */

public class DownloadUtils {

    private static final String TAG = "DownloadUtils";

    public static final boolean checkDownloadEnable(Context context){
       int state =  context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
       if(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
               state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER ||
               state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED){
           return false;
       }
       return true;
    }

    public static final void openDownloadEnableSettings(Context context){
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("com.android.providers.downloads"));
        context.startActivity(intent);
    }

    public static final void download(Context context,String url){
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        File file = new File(Environment.DIRECTORY_DOWNLOADS + "/update.apk");
        DebugUtil.d(TAG, "download::file = " + file.exists());
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri downloadFileUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            downloadFileUri = FileProvider.getUriForFile(context,"com.zxl.test_weather",file);
        }else{
            downloadFileUri = Uri.fromFile(file);
        }

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"update.apk");

        request.setTitle("更新");
        request.setDescription("下载更新包");
        request.setMimeType("application/vnd.android.package-archive");
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(SharePreUtils.getInstance(context).getDownloadId());
        long id = downloadManager.enqueue(request);
        SharePreUtils.getInstance(context).saveDownloadId(id);
    }

    public static final void installApk(Context context, long downloadApkId) {
        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);

        DebugUtil.d(TAG, "installApk::downloadFileUri = " + downloadFileUri);


        if (downloadFileUri != null) {
            DebugUtil.d(TAG, "installApk::uri = " + downloadFileUri.toString());
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            if ((Build.VERSION.SDK_INT >= 24)) {//判读版本是否在7.0以上
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (install.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(install);
            } else {
                DebugUtil.d(TAG, "自动安装失败，请手动安装");
            }
        } else {
            DebugUtil.d(TAG, "download error");
        }
    }
}
