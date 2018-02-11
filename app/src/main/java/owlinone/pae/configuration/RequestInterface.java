package owlinone.pae.configuration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {

    @POST("OwlinOnePAE/")
    Call<ServerResponse> operation(@Body ServerRequest request);

}
