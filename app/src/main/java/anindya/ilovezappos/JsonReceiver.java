package anindya.ilovezappos;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * JsonReceiver
 * This AsyncTask is used to retrieve the JSON file from the Zappos server.
 * Created by Anindya on 2/7/2017.
 */

class JsonReceiver extends AsyncTask<String,Void, String> {

    private String TAG = "Zappos-JsonReceiver";

    //Attributes used to retrieve data.
    private static final String KEY = "b743e26728e16b81da139182bb2094357c31d331";
    private static final String URL = "https://api.zappos.com/Search?";
    private static String uri, query;

    interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;
    JsonReceiver(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {

        URL zapposPt;
        params[0] = params[0].replaceAll(" ","+");
        uri = URL + "term=" + params[0] + "&key=" + KEY;
        query = params[0];
        String response = "";
        try {
            zapposPt = new URL(uri);
            HttpsURLConnection conn = (HttpsURLConnection) zapposPt.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == 200) {
                InputStream in = new BufferedInputStream(conn.getInputStream());
                response = convertStreamToString(in);
            } else {
                Log.e(TAG,"Failure: Response code = "+conn.getResponseCode()+" "+conn.getResponseMessage());
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL: "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
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

    public static String getQuery() {
        return query;
    }
}

