package hxb.xb_testandroidfunction.test_apk_update.retrofit_update;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by hxb on 2018/8/24
 */
public interface IApiService {
    @GET
    Call<ResponseBody> downloadFile(@Url String url);
}
