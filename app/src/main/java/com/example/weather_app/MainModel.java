package com.example.weather_app;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class MainModel {

    public class Main {

        @SerializedName("temp")
        @Expose
        private Double temp;
        @SerializedName("pressure")
        @Expose
        private Integer pressure;
        @SerializedName("humidity")
        @Expose
        private Integer humidity;
        @SerializedName("temp_min")
        @Expose
        private Double tempMin;
        @SerializedName("temp_max")
        @Expose
        private Double tempMax;

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Main withTemp(Double temp) {
            this.temp = temp;
            return this;
        }

        public Integer getPressure() {
            return pressure;
        }

        public void setPressure(Integer pressure) {
            this.pressure = pressure;
        }

        public Main withPressure(Integer pressure) {
            this.pressure = pressure;
            return this;
        }

        public Integer getHumidity() {
            return humidity;
        }

        public void setHumidity(Integer humidity) {
            this.humidity = humidity;
        }

        public Main withHumidity(Integer humidity) {
            this.humidity = humidity;
            return this;
        }

        public Double getTempMin() {
            return tempMin;
        }

        public void setTempMin(Double tempMin) {
            this.tempMin = tempMin;
        }

        public Main withTempMin(Double tempMin) {
            this.tempMin = tempMin;
            return this;
        }

        public Double getTempMax() {
            return tempMax;
        }

        public void setTempMax(Double tempMax) {
            this.tempMax = tempMax;
        }

        public Main withTempMax(Double tempMax) {
            this.tempMax = tempMax;
            return this;
        }

    }
}
