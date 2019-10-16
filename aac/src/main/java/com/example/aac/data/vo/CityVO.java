package com.example.aac.data.vo;

/**
 * Created by hxb on 2019-08-23.
 */
public class CityVO {
    private String cityName = "";
    private String cityId = "";

    public String getCityName() {
        return cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public String toString() {
        return "CityVO{" +
                "cityName='" + cityName + '\'' +
                ", cityId='" + cityId + '\'' +
                '}';
    }
}
