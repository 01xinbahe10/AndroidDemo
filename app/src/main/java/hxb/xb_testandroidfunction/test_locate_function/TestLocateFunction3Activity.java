package hxb.xb_testandroidfunction.test_locate_function;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;

import hxb.xb_testandroidfunction.R;

/**
 * Created by hxb on 2018/11/26
 */
public class TestLocateFunction3Activity extends FragmentActivity{

    private static final String TAG = "TestLocateFunction";
    private final  int PERMISSION_REQUEST_CODE_STORAGE = 10000;

    LocationManager locationManager; //系统定位
    private String mLongitude = ""; // 经度
    private String mLatitude = ""; // 维度

    LocationManager lm; //系统定位
    private int minTime = 20000;
    private int minDistance = 20;

    private long lastMillis = 0;// 上一次提交的时间
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_locate_function);
    }

//    判断GPS是否打开，若没有打开，就手动打开：
    private void initLocalLibView() {
        //获取定位服务
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //检查定位是否被打开
        boolean gpsIsOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsIsOpen) {
            //定位 并请求接口
            //定位
            startLocation();
        } else {
            Toast.makeText(this, "请打开地理位置，我们帮你找到最近的图书馆", Toast.LENGTH_SHORT).show();
//            T.showShort("请打开地理位置，我们帮你找到最近的图书馆");// TODO: 2017/8/15
        }
    }

    private void startLocation() {

        // 为获取地理位置信息时设置查询条件 是按GPS定位还是network定位
        String bestProvider = getProvider();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            return;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        mLongitude = String.valueOf(location.getLongitude());
        mLatitude = String.valueOf(location.getLatitude());

        Log.e(TAG,"Location" + "纬度：" + mLatitude + "\n" +  "经度:  " + mLongitude);


    }



    /**
     * 定位查询条件
     * 返回查询条件 ，获取目前设备状态下，最适合的定位方式
     */
    private String getProvider() {
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        //Criteria.ACCURACY_FINE,当使用该值时，在建筑物当中，可能定位不了,建议在对定位要求并不是很高的时候用Criteria.ACCURACY_COARSE，避免定位失败
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(false);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
        return locationManager.getBestProvider(criteria, true);
    }

    /**
     * 6.0动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PERMISSION_REQUEST_CODE_STORAGE == requestCode) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                requestPermission();
                Toast.makeText(this,"没有开启定位权限，无法找到附近图书馆",Toast.LENGTH_SHORT).show();
            } else {
                startLocation();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void getLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
            // 返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        // 为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        // 获取位置信息
        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = lm.getLastKnownLocation(bestProvider);
        /**
         * 监听状态
         */
        lm.addGpsStatusListener(listener);

        /**
         * 绑定监听，有4个参数 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种 参数2，位置信息更新周期，单位毫秒
         * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息 参数4，监听
         * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新。
         * 1秒更新一次，或最小位移变化超过1米更新一次；
         * 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep
         * (10000);然后执行handler.sendMessage(),更新位置
         */

        if (lm.getProvider(LocationManager.GPS_PROVIDER) != null) {
            Log.d("GPS_PROVIDER执行：", "GPS方法执行。。。。。。。。");
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        } else if (lm.getProvider(LocationManager.NETWORK_PROVIDER) != null) {
            Log.d("NETWORK执行：", "NETWORK方法执行。。。。。。。。");
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener);
        }
    }

    // 位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " +"经度：" + location.getLongitude() + "\n纬度：" + location.getLatitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                // GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");

                    break;
                // GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                // GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }

    };

    // 状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                // 第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                // 卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    long currenMillis = System.currentTimeMillis();// 当前时间
                    if ((currenMillis - lastMillis) > minTime) {
                        lastMillis = currenMillis;
                        // 获取当前状态
                        GpsStatus gpsStatus = lm.getGpsStatus(null);
                        // 获取卫星颗数的默认最大值
                        int maxSatellites = gpsStatus.getMaxSatellites();
                        // 创建一个迭代器保存所有卫星
                        Iterator<GpsSatellite> iters = gpsStatus.getSatellites()
                                .iterator();
                        int count = 0;
                        while (iters.hasNext() && count <= maxSatellites) {
                            count++;
                        }
                        Log.i(TAG, "卫星状态改变,搜索到：" + count + "颗卫星");
                    }
                    break;
                // 定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    break;
                // 定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
            }
        }

    };




    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }



}
