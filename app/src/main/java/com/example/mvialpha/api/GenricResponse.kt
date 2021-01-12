package com.example.mvialpha.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GenricResponse (
    @SerializedName("response")
    @Expose
    var response: String
)