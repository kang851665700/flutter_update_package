package com.update.dealog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.update.R;
import com.update.utils.FileUtils;
import com.update.views.NumberProgressBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UpAppDialog extends Dialog implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Context context;
    private TextView tv_title;
    private TextView tv_update_info;
    private ImageView iv_close;
    private TextView mUpdateOkButton;
    private NumberProgressBar mNumberProgressBar;
    private LinearLayout ll_close;
    private String url;
    private String edition;

    public UpAppDialog(Context context) {
        super(context,R.style.CustomDialog);
        this.context = context;
        OkHttpUtils.getInstance() .init(this.context).debug(true, "okHttp").timeout(20 * 1000);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();
    }
    /**
     * 初始化界面控件
     */
    private void initView() {
        //标题
        tv_title =  findViewById(R.id.tv_title);
        //提示内容
        tv_update_info = findViewById(R.id.tv_update_info);
        //关闭按钮
        iv_close = findViewById(R.id.iv_close);
        //更新按钮
        mUpdateOkButton = findViewById(R.id.btn_ok);
        //进度条
        mNumberProgressBar = findViewById(R.id.npb);
        //关闭按钮+线 的整个布局
        ll_close = findViewById(R.id.ll_close);
    }
    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        mUpdateOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Acp.getInstance(context).request(new AcpOptions.Builder()
                                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                                .build(),
                        new AcpListener() {
                            @Override
                            public void onGranted() {
                                mUpdateOkButton.setVisibility(View.GONE);
                                mNumberProgressBar.setVisibility(View.VISIBLE);
                                setUpdate();
                            }

                            @Override
                            public void onDenied(List<String> permissions) {
                                Toast.makeText(context, "权限被禁止，无法下载APK", Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     *
     * @param Title 版本
     * @param content 内容
     */
    public void setTitleT(String Title,String content){
        tv_title.setText(Title);
        tv_update_info.setText(content);
    }
    /**
     *
     * @param url 下载地址
     * @param edition 版本
     */
    public void setUpdateUrl(String url,String edition){
        this.url = url;
        this.edition = edition;
    }

    /**
     *
     * @param force true是代表强制false是代表不强制
     */
    public void setWhetherForce(String force){
        if(force.equals("true")){
            ll_close.setVisibility(View.GONE);
        }else {
            ll_close.setVisibility(View.VISIBLE);
        }
    }


    public void setUpdate(){
        String appname = FileUtils.getApkName();
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String target = appDir + File.separator + "updateAPK";
        OkHttpUtils.get().url(url).build().execute(new FileCallBack(target, appname) {
            @Override
            public void inProgress(float progress, long total, int id) {
                mNumberProgressBar.setProgress(Math.round(progress * 100));
                mNumberProgressBar.setMax(100);
            }

            @Override
            public void onError(Call call, Response response, Exception e, int id) {

            }

            @Override
            public void onResponse(final File response, int id) {
                //安装
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        installApk(response);
                    }
                });
            }
        });
    }

    public void installApk(File file) {
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(this.context, "com.wy.toy.FileProvider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        this.context.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 222) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpdate();
            } else {
                Toast.makeText(context, "权限被禁止，无法下载APK", Toast.LENGTH_LONG).show();
            }
        }
    }
}
