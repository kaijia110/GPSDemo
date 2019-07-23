package com.likai.gpsdemo;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 创建人 ${kaijia}
 * 日期 2019/6/19
 */
public class HttpPostUtils {
    public static void getPostMsg(final String url, final String json) {
        new Thread() {
            @Override
            public void run() {
                Log.e("传入的" ,"json：" + json);
                MediaType JSON = MediaType.parse("application/json");
                //申明给服务端传递一个json串
                //创建一个OkHttpClient对象
                OkHttpClient okHttpClient = new OkHttpClient();
                //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
                //json为String类型的json数据
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json));
                //创建一个请求对象
//                        String format = String.format(KeyPath.Path.head + KeyPath.Path.waybillinfosensor, username, key, current_timestamp);
                String format = String.format(url);
                Log.e("这是个" ,"什么东西：" + format);
                Request request = new Request.Builder()
                        .url(format)
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //DialogUtils.showPopMsgInHandleThread(Release_Fragment.this.getContext(), mHandler, "数据获取失败，请重新尝试！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String string = response.body().string();
                        if (string != null) {
                            Log.e("返回的" , "请求数据：" + string);

                        } else {
                            Log.e("空数" , "据：");
                        }
                    }
                });
            }
        }.start();
    }
}
