package com.popcorp.parser.skidkaonline.net;

import com.popcorp.parser.skidkaonline.dto.SaleCommentDTO;
import com.popcorp.parser.skidkaonline.dto.SaleCommentsDTO;
import okhttp3.ResponseBody;
import retrofit2.http.*;
import rx.Observable;

public interface API {

    @GET("/cities/")
    Observable<ResponseBody> getAllCities(@Query(value = "is_ajax", encoded = true) int isAjax);

    @GET("/cities/")
    Observable<ResponseBody> getCitiesForPage(@Query(value = "a", encoded = true) String page);

    @GET("{path}")
    Observable<ResponseBody> getCity(@Path(value = "path", encoded = true) String path);

    @GET("{city}")
    Observable<ResponseBody> getShops(@Path(value = "city", encoded = true) String cityUrl);

    @GET("{city}{shop}")
    Observable<ResponseBody> getSales(
            @Path(value = "city", encoded = true) String cityUrl,
            @Path(value = "shop", encoded = true) String shopUrl,
            @Query("is_ajax") int isAjax,
            @Query("page") int page
    );


    @FormUrlEncoded
    @POST("/product/ajaxaddcomment/")
    Observable<SaleCommentDTO> sendComment(@Field("username") String author,
                                           @Field("comment") String text,
                                           @Field("city_id") int cityId,
                                           @Field("product_id") int saleId,
                                           @Field("is_ajax") int isAjax


    );

    @POST("/product/ajaxgetcomments/")
    Observable<SaleCommentsDTO> getComments(@Field("product_id") int saleId,
                                            @Field("is_ajax") int isAjax);
}
