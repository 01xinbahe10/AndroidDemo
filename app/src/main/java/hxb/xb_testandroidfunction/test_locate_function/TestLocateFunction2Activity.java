package hxb.xb_testandroidfunction.test_locate_function;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import hxb.xb_testandroidfunction.R;
import hxb.xb_testandroidfunction.test_locate_function.service.LocationUpdateService;
import hxb.xb_testandroidfunction.test_locate_function.util.HttpUtil;

import static java.lang.Thread.sleep;

/**
 * Created by hxb on 2018/11/26
 *
 * 用定位的方式获取国别
 */
public class TestLocateFunction2Activity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "POSITION";
    public static final int SHOW_LOCATION = 0;//更新文字式的位置信息
    public static final int SHOW_LATLNG = 1; //更新经纬坐标式的位置信息
    private TextView positionTextView;
    private TextView positionLatLng;
    private LocationManager locationManager;
    private String provider;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOCATION:
                    Log.d(TAG, "showing the positio>>>>>");
                    String currentPosition = (String) msg.obj;
                    positionTextView.setText(currentPosition);
                    Log.d(TAG, "Has show the position...>>>>....");
                    break;
                case SHOW_LATLNG:
                    String latlng = (String) msg.obj;
                    positionLatLng.setText(latlng);
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_locate_function);
        findViewById(R.id.btn1).setOnClickListener(this);
        positionTextView = findViewById(R.id.tv1);
        positionLatLng = findViewById(R.id.tv2);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        provider = LocationManager.NETWORK_PROVIDER;
//        provider = LocationManager.GPS_PROVIDER;
//        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
//            provider = LocationManager.GPS_PROVIDER;
//        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
//            provider = LocationManager.NETWORK_PROVIDER;
//        }else {
//            //当没有可用的位置提供器时，弹出Toast提示用户
//            Toast.makeText(this, "No Location provider to use", Toast.LENGTH_SHORT).show();
//            return;
//        }



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10000);
            }
        }
//        Location location = locationManager.getLastKnownLocation(provider);
        // 为获取地理位置信息时设置查询条件
        String bestProvider = locationManager.getBestProvider(getCriteria(), true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            //显示当前设备的位置信息
            Log.d(TAG, "location!=null");
            showLocation(location);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 500, locationListener);
//        locationManager.requestLocationUpdates(provider, 5000, 500, locationListener);
        Log.d(TAG, "Running....");
        startService(new Intent(this, LocationUpdateService.class));//启动位置定位服务
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //关闭程序时将监听移除
            locationManager.removeUpdates(locationListener);
        }
    }


    //LocationListener 用于当位置信息变化时由 locationManager 调用
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            //更新当前设备的位置信息
            showLocation(location);
            getAddress(location.getLongitude(), location.getLatitude());

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onProviderDisabled: 22222222222222222222222 " );

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onProviderEnabled: 3333333333333333333333333 " );

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onStatusChanged: 444444444444444444444444444 " );
        }

    };

    private void showLocation(final Location location) {
        //显示实际地理位置
        //开启线程来发起网络请求
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    String request = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
                    request += location.getLatitude() + "," + location.getLongitude() + "&sensor=false";
                    String response = HttpUtil.sendHttpRequest(TestLocateFunction2Activity.this, request);
                    parseJSONResponse(response);

                } catch (Exception e) {
                    Log.d(TAG, "showLocation: the inptuStream is wrong!");
                    e.printStackTrace();
                }
            }

        }).start();
        //显示经纬度坐标
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String position = "";
                position = "Latitude=" + location.getLatitude() + "\n"
                        + "Longitude=" + location.getLongitude();
                Message msg = new Message();
                msg.what = SHOW_LATLNG;
                msg.obj = position;
                handler.sendMessage(msg);
            }

        }).start();
    }


    //解析JSON数据
    private void parseJSONResponse(String response) {
        try {
            Log.d(TAG, "parseJSONResponse: getting the jsonObject...");
            JSONObject jsonObject = new JSONObject(response);
            //获取results节点下的位置
            Log.d(TAG, "parseJSONResponse: Getting the jsongArray...");
            JSONArray resultArray = jsonObject.getJSONArray("results");
            Log.d(TAG, "parseJSONResponse: Got the JSONArray...");
            if (resultArray.length() > 0) {
                JSONObject subObject = resultArray.getJSONObject(0);
                //取出格式化后的位置信息
                String address = subObject.getString("formatted_address");
                Message message = new Message();
                message.what = SHOW_LOCATION;
                message.obj = "您的位置:" + address;
                Log.d(TAG, "showLocation:Sending the inputStream...");
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSONResponse: something wrong");
            e.printStackTrace();
        }

    }

    //获取地理位置名
    public String getAddress(double lnt, double lat) {
        Geocoder geocoder = new Geocoder(this);
        boolean falg = geocoder.isPresent();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //根据经纬度获取地理位置信息---这里会获取最近的几组地址信息，具体几组由最后一个参数决定
            List<Address> addresses = geocoder.getFromLocation(lat, lnt, 1);
            Log.e(TAG, "onLocationChanged: ----------------------  "+addresses.size());
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    //每一组地址里面还会有许多地址。这里我取的前2个地址。xxx街道-xxx位置
                    if (i == 0) {
                        stringBuilder.append(address.getAddressLine(i)).append("-");
                    }

                    if (i == 1) {
                        stringBuilder.append(address.getAddressLine(i));
                        break;
                    }
                }

//                stringBuilder.append(address.getCountryName()).append("_");//国家
//                stringBuilder.append(address.getFeatureName()).append("_");//周边地址
//                stringBuilder.append(address.getLocality()).append("_");//市
//                stringBuilder.append(address.getPostalCode()).append("_");
//                stringBuilder.append(address.getCountryCode()).append("_");//国家编码
//                stringBuilder.append(address.getAdminArea()).append("_");//省份
//                stringBuilder.append(address.getSubAdminArea()).append("_");
//                stringBuilder.append(address.getThoroughfare()).append("_");//道路
//                stringBuilder.append(address.getSubLocality()).append("_");//香洲区
//                stringBuilder.append(address.getLatitude()).append("_");//经度
//                stringBuilder.append(address.getLongitude());//维度

                Log.e(TAG, "getAddress: " + address.getCountryCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


     /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(true);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
//                locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(5000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.i("gps", "当前经纬度"+ LocationUpdateService.latitude+":"+LocationUpdateService.longitude);
                    }
                }).start();
                break;
        }
    }




}
