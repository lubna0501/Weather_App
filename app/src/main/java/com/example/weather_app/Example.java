package com.example.weather_app;

import com.google.gson.annotations.SerializedName;

public class Example {
    @SerializedName("main")
    MainModel.Main main;

    public MainModel.Main getMain() {
        return main;
    }

    public void setMain(MainModel.Main main) {
        this.main = main;
    }
}
