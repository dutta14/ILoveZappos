package anindya.ilovezappos;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import anindya.ilovezappos.databinding.ActivityHomeBinding;

import static android.view.View.*;

class Task extends AsyncTask<String,Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    Task(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {

        URL zapposPt = null;
        String response="";
        try {
            zapposPt = new URL("https://api.zappos.com/Search?term="+params[0]+"&key=b743e26728e16b81da139182bb2094357c31d331");
            HttpsURLConnection conn =
                    (HttpsURLConnection) zapposPt.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertStreamToString(in);
            } else {
                Log.e("anindya","Failure: Response code = "+conn.getResponseCode()+" "+conn.getResponseMessage());
            }

        } catch (MalformedURLException e) {
            Log.e("anindya", "Malformed URL");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

class ImageLoader extends AsyncTask<String, Void, Bitmap> {

     public interface ImageResponse {
         void processFinish (Bitmap op);
     }

    ImageResponse delegate = null;

    ImageLoader(ImageResponse delegate) {
        this.delegate = delegate;
    }


    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bmp=null;
        try {
            InputStream in = new URL(params[0]).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
             // log error
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        delegate.processFinish(result);
    }
}

public class Home extends ActionBarActivity {
    ActivityHomeBinding binding;
    FloatingActionButton fab;
    LinearLayout info;
    Context mContext;
    SearchView sView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        handleIntent(getIntent());
        mContext = getApplicationContext();
        setupFab();
    }

    private void setupFab() {
        fab = (FloatingActionButton) findViewById(R.id.addtocart);
        fab.hide();
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shrink);
                view.startAnimation(anim);
                CoordinatorLayout layout = (CoordinatorLayout)findViewById(R.id.main_content);
                Snackbar.make(layout, mContext.getText(R.string.added_to_cart), Snackbar.LENGTH_SHORT).show();
            }
        });

        info = (LinearLayout) findViewById(R.id.info);
        info.setVisibility(GONE);

        TextView price = (TextView) findViewById(R.id.original_price);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            final String query = intent.getStringExtra(SearchManager.QUERY);
            TextView noResult = ((TextView) findViewById(R.id.message));
            noResult.setText("Loading result for "+ query);
            sView.onActionViewCollapsed();
            new Task(new Task.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    parseJSON(output);
                }
            }).execute(query);

        }
    }

    private void parseJSON(String output) {
        try {
            JSONObject obj = new JSONObject(output);
            JSONArray contacts = obj.getJSONArray("results");
            JSONObject result = contacts.getJSONObject(0);
            TextView noResult = ((TextView) findViewById(R.id.message));
            LinearLayout results = (LinearLayout) findViewById(R.id.details);

            if(result.length() == 0) {
                noResult.setText(R.string.no_match);
                noResult.setVisibility(VISIBLE);
                results.setVisibility(GONE);
            }
            else {
                results.setVisibility(VISIBLE);
                noResult.setVisibility(GONE);
                fab.show();
                info.setVisibility(VISIBLE);
                String brandName = result.getString("brandName");
                String image = result.getString("thumbnailImageUrl");
                String productId = result.getString("productId");
                String originalPrice = result.getString("originalPrice");
                String styleId = result.getString("styleId");
                String colorId = result.getString("colorId");
                String price = result.getString("price");
                String percentOff = result.getString("percentOff");
                String productUrl = result.getString("productUrl");
                String productName = result.getString("productName");

                final Product p = new Product(brandName, null, productId, originalPrice, styleId, colorId, price, percentOff, productUrl, productName);


                new ImageLoader(new ImageLoader.ImageResponse() {

                    @Override
                    public void processFinish(Bitmap op) {
                        p.setImage(new BitmapDrawable(op));
                        binding.setProduct(p);
                    }
                }).execute(image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager sMgr = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sView = (SearchView) (menu.findItem(R.id.search)).getActionView();
        sView.setSearchableInfo(sMgr.getSearchableInfo(getComponentName()));

        return true;
    }
}
