package gr.academic.city.sdmd.asynctasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String IMAGE_URL = "http://vignette3.wikia.nocookie.net/appium/images/a/a3/Tux_toilet.png/revision/latest?cb=20130816172512";

    private static final String LOG_TAG = "AsyncTasks-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageDownload();
            }
        });
    }

    private void startImageDownload() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadImageTask().execute(IMAGE_URL);
        } else {
            // display error
            Toast.makeText(this, R.string.err_no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadImage(urls[0]);
            } catch (IOException e) {
                return null;
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Log.e(LOG_TAG, "Unable to retrieve image. URL may be invalid.");
                return;
            }

            ImageView imageView = (ImageView) findViewById(R.id.img_downloaded);
            imageView.setImageBitmap(result);
        }
    }

    private Bitmap downloadImage(String imageUrl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(LOG_TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            return bitmap;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
