package anindya.ilovezappos;

import anindya.ilovezappos.model.ResultList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by anind on 5/13/2017.
 */

public interface EndPointInterface {

    @GET("Search")
    Call<ResultList> getResults(@Query("term") String term, @Query("key") String key);
}
