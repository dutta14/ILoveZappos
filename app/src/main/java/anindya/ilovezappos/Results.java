package anindya.ilovezappos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import anindya.ilovezappos.model.Result;
import anindya.ilovezappos.model.ResultList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Results extends AppCompatActivity {

    public static String BASE_URL;
    public static String KEY;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        String term = getIntent().getStringExtra("keyword");

        mContext = getApplicationContext();
        BASE_URL = mContext.getString(R.string.url);
        KEY = mContext.getString(R.string.api_key);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EndPointInterface apiService = retrofit.create(EndPointInterface.class);

        Call<ResultList> call = apiService.getResults(term, KEY);
        call.enqueue(new Callback<ResultList>() {
            @Override
            public void onResponse(Call<ResultList> call, final Response<ResultList> response) {
                List<Result> list = response.body().getResults();

                Result[] array = list.toArray(new Result[list.size()]);
                final MyAdapter adapter = new MyAdapter(mContext, array);
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    Result result = adapter.getItem(position);
                    Intent intent = new Intent(mContext, Details.class);
                    intent.putExtra("mResult", result);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(Call<ResultList> call, Throwable t) {}
        });

        FloatingActionButton cart = (FloatingActionButton) findViewById(R.id.cart);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(Results.this, ViewCart.class);
            startActivity(intent);
        });
    }

   public static class MyAdapter extends ArrayAdapter<Result> {

        private Result[] result;

       Context mContext;
        MyAdapter(Context context, Result[] result) {
            super(context, R.layout.result_row, result);
            mContext = context;
            this.result = result;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.result_row, parent, false);
            TextView productName = (TextView) rowView.findViewById(R.id.label);
            ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
            productName.setText(result[position].getProductName());
            Picasso.with(mContext).load(result[position].getThumbnailImageUrl()).into(icon);

            return rowView;
        }
    }

}
