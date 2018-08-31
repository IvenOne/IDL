package iven.com.idownapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import iven.com.idl.AppDownloadManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {

    private TextView tv;

    private long mTaskId;
    private AppDownloadManager appDownloadManager;
    final String down_url = "http://www.htfy8.com/public/app_release_v2.0.0_KFX2_08250439.apk";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appDownloadManager = new AppDownloadManager(this,"为了正常升级 [优衣库] APP，请点击设置按钮，允许安装未知来源应用，本功能只限用于 [优衣库] APP版本升级",BuildConfig.APPLICATION_ID);
        tv = findViewById(R.id.content);

        OkHttpClient client = new OkHttpClient();
        String url = "http://192.168.1.167:8087/version/nofilter/queryLastest?versionType=android";

        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("iven", e.toString());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();
                Log.i("iven", response.toString());
                tv.post(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("iven", responseStr);
                        tv.setText(responseStr);
                        try {
                            JSONObject jsonObject = new JSONObject(responseStr);
                            String versionCode = new JSONObject(jsonObject.optString("data")).optString("versionCode");

                            if (Integer.parseInt(versionCode) > 1) {
//                        UpdateService.Builder.create(down_url).setStoreDir("update").setIsSendBroadcast(true)
//                                        .setDownloadSuccessNotificationFlag(Notification.DEFAULT_SOUND)
//                                        .setDownloadErrorNotificationFlag(Notification.DEFAULT_SOUND)
//                                        .build(getBaseContext());
//                        Aria.download(this)
//                                .load(down_url)     //读取下载地址
//                                .setDownloadPath("/update") //设置文件保存的完整路径
//                                .start();   //启动下载
                                showUpdateDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle("更新")
                .setCancelable(true)
                .setMessage("就问你更不更新吧")
                .setNegativeButton("就不嫩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("好的啊", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        appDownloadManager.setUpdateListener(new AppDownloadManager.OnUpdateListener() {
                            @Override
                            public void update(int currentByte, int totalByte) {
                                tv.setText(currentByte+"/"+totalByte);
                                if ((currentByte == totalByte) && totalByte != 0) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        appDownloadManager.downloadApk(down_url,"下载中","版本更新拉拉阿拉啦啦啦啦啦啦阿拉啦啦啦啦");
                    }
                });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (appDownloadManager != null) {
            appDownloadManager.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (appDownloadManager != null) {
            appDownloadManager.onPause();
        }
    }
}

