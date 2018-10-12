package com.zxl.test1.http.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by uidq0955 on 2017/9/23.
 */

public interface HttpApiService {

    @GET("TestServlet/{id}")
    Call test(@Path("id") int id);
}
