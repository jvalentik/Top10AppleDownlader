package org.jvk.top10downlader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String APPLE_RSS = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
    private Button btnParse;
    private ListView listMain;
    private String fileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnParse = (Button) findViewById(R.id.btnParse);
        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add parse activation code
                ParseApplications parseApplications = new ParseApplications(fileContents);
                parseApplications.process();
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainThread.this, R.layout.list_item, parseApplications.getApplications());
                listMain.setAdapter(arrayAdapter);

            }
        });
        listMain = (ListView) findViewById(R.id.xmlList);
        DownloadData downloadData = new DownloadData();
        downloadData.execute(APPLE_RSS);
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
        private static final String TAG = "DownloadData";

        @Override
        protected String doInBackground(String... params) {
            fileContents = downloadXMLFile(params[0]);
            if (fileContents == null) {
                Log.d(TAG, "Error downloading");
            }
            return fileContents;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder buffer = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "ResponseCode: " + response);
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
                Log.d(TAG, "IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.d(TAG, e.getMessage());
            }
            return null;
        }
    }
}

