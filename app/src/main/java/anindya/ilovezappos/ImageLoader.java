package anindya.ilovezappos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * This AsyncTask is used to retrieve the image provided in the http URL in the JSON file.
 * Created by Anindya on 2/7/2017.
 */

class ImageLoader extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "Zappos-ImageLoader";

    interface ImageResponse {
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
            Log.e(TAG, e.getMessage());
        }
        return bmp;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        delegate.processFinish(result);
    }
}

