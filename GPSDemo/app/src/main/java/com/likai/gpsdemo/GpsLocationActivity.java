package com.likai.gpsdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * GPS定位
 */
public class GpsLocationActivity extends AppCompatActivity implements PermissionUtils.PermissionCallbacks {


    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.longtitude)
    TextView longtitude;
    @BindView(R.id.latitude)
    TextView latitude;
    @BindView(R.id.height)
    TextView height;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.ip_text)
    EditText ipText;
    private LocationManager mLocationManager;

    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private static final int REQUEST_PERMISSION_CODE = 12;
    private SharedPreferencesHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_location);
        ButterKnife.bind(this);
        helper = new SharedPreferencesHelper(this, "shareFile");
        String ipaddress = (String) helper.getSharedPreference("ipaddress", "202.142.28.62:8085");
        if (!ipaddress.equals("")){
            ipText.setText(ipaddress);
        }
    }

    public void doClick(View view) {
        String ipAddress = ipText.getText().toString();
        if (ipAddress.equals("")) {
            Toast.makeText(this, "请输入ip地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PermissionUtils.hasPermissions(this, permissions)) {
            PermissionUtils.requestPermissions(this, REQUEST_PERMISSION_CODE, permissions);
        } else {
            button.setText("正在定位……");
            startLocate();
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocate() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) { //GPS已开启
            /**
             * 绑定监听
             * 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种，前者是GPS,后者是GPRS以及WIFI定位
             * 参数2，位置信息更新周期.单位是毫秒
             * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
             * 参数4，监听
             * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
             */
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            Toast.makeText(this, "请打开GPS", Toast.LENGTH_SHORT).show();
            button.setText("请打开GPS");
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //位置信息变化时触发
            button.setText("定位成功");
//            202.142.28.62:8085
            String ipAddress = ipText.getText().toString();
            helper.put("ipaddress",ipAddress);
            String url = "http://" + ipAddress + "/api/v1/Erc3pxNX0HdVAnQE78fV/telemetry";
            Log.e("xyh", "定位方式：" + location.getProvider());
            type.setText("定位方式：" + location.getProvider());
            Log.e("xyh", "纬度：" + location.getLatitude());
            latitude.setText("纬度：" + location.getLatitude());
            Log.e("xyh", "经度：" + location.getLongitude());
            longtitude.setText("经度：" + location.getLongitude());
            Log.e("xyh", "海拔：" + location.getAltitude());
            height.setText("海拔：" + location.getAltitude());
            Log.e("xyh", "时间：" + location.getTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String nowtime = format.format(location.getTime());
            time.setText("时间：" + nowtime);
            ZuoBiaoJson zuoBiaoJson = new ZuoBiaoJson();
            zuoBiaoJson.setLatitude(location.getLatitude());
            zuoBiaoJson.setLongitude(location.getLongitude());
            String json = new Gson().toJson(zuoBiaoJson);
            HttpPostUtils.getPostMsg(url, json);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //GPS状态变化时触发
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.e("onStatusChanged", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.e("onStatusChanged", "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.e("onStatusChanged", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            //GPS开启时触发
            Log.e("xyh", "onProviderEnabled: ");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //GPS禁用时触发
            Log.e("xyh", "onProviderDisabled: ");
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsAllGranted(int requestCode, List<String> perms, boolean isAllGranted) {
        if (isAllGranted) {
            startLocate();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (PermissionUtils.somePermissionPermanentlyDenied(this, perms)) {
            PermissionUtils.showDialogGoToAppSettting(this);
        } else {
            PermissionUtils.showPermissionReason(requestCode, this, permissions, "需要定位权限");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}