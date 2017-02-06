package org.jvk.top10downlader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        private String fileContents;

        @Override
        protected String doInBackground(String... params) {
            fileContents = downloadXMLFile(params[0]);
            if (fileContents == null) {
                Log.d("DownloadData", "Error downloading");
            }
            return fileContents;
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d("DownloadData", "ResponseCode: " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int charRead;
                char[] inputBuffer = new char[512];
                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }
                    buffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }
                return buffer.toString();
            } catch (IOException e) {
                Log.d("DownloadData", "IO Exception reading data: " + e.getMessage());
                return null;
            }
        }
    }
}

