package com.example.ibra.assignment2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSON extends Activity {

    EditText jsonEdit;
    Button jsonSearch;
    TextView tv;
    SQLiteDatabase db;
    String dbname = "ASSIGNMENT2";
    String tablename = "SUBJECTS";
    String col1 = "ID";
    String col2 = "CODE";
    String [] codes = {"HDR972", "FND115", "ECON215", "CSCI235", "CSCI204", "FND111", "CSCI336", "ECTE363", "ECTE202", "MMC928", "MGMT930", "TBS953-W", "TBS804", "ECON216", "FIN324", "CSCI319", "FIN322", "LAW100", "MARK217", "FIN928"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);

        jsonEdit = (EditText)findViewById(R.id.jsonSubjectEdit);
        jsonSearch = (Button)findViewById(R.id.jsonSearchBtn);
        tv = (TextView)findViewById(R.id.jsonoutput);

        db = openOrCreateDatabase(dbname, db.CREATE_IF_NECESSARY, null);
        createTable();

        search();
    }

    private void createTable() {
        String createTable = "CREATE TABLE IF NOT EXISTS " + tablename + "(" + col1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + col2 + " TEXT)";
        db.execSQL(createTable);
    }

    private void search() {
        jsonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = jsonEdit.getText().toString();
                boolean included = false;
                if (code.equals("")){
                    included = true;
                }
                else
                {
                    for (int i=0; i<codes.length; i++)
                    {
                        if (codes[i].equals(code))
                            included = true;
                    }
                    Cursor c = db.rawQuery("SELECT * FROM " + tablename, null);
                    if (c.getCount() != 0)
                    {
                        c.moveToFirst();
                        for (int i=0; i<c.getCount(); i++){
                            if (c.getString(1).equals(code)){
                                included = true;
                            }
                            c.moveToNext();
                        }
                    }
                }

                if (included == true)
                {
                    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    String url = "https://my.uowdubai.ac.ae/restful/subject/list/";
                    String urlCode = "https://my.uowdubai.ac.ae/restful/subject/list/" + code + "/";
                    if (networkInfo != null && networkInfo.isConnected()){
                        if (code.equals(""))
                            new downloadURL().execute(url);
                        else
                            new downloadURL().execute(urlCode);
                    } else {
                        Toast.makeText(JSON.this, "Not connected", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    ContentValues values = new ContentValues();
                    values.put(col2, code);
                    long x = db.insert(tablename, "", values);
                    Toast.makeText(JSON.this, "Record inserted with ID: " + x, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "application/json");

            // Starts the query
            conn.connect();
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;
            // Makes sure that the InputStream is closed after the app is // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[1024];
        reader.read(buffer);
        return new String(buffer);
    }

    private class downloadURL extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try{
                return(downloadUrl(params[0]));
            }catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv.setText(processJSON(s));
        }
    }

    private String processJSON(String result){
        JSONObject data = null;
        String formattedString = new String();
        if (jsonEdit.getText().toString().equals(""))
        {
            formattedString = formattedString + "FROM WEB" + "\n";
            try {
                data = new JSONObject(result);
                for (int i=0; i<data.length(); i++)
                {
                    formattedString = formattedString + codes[i] + ": " + data.getString(codes[i]) + "\n";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            formattedString = formattedString + "\n" + "FROM DATABASE" + "\n";
            Cursor c = db.rawQuery("SELECT * FROM " + tablename, null);
            if (c.getCount() != 0)
            {
                c.moveToFirst();
                for (int i=0; i<c.getCount(); i++){
                    formattedString = formattedString + c.getString(1) + "\n";
                    c.moveToNext();
                }
            }
        }
        else
        {
            String code = jsonEdit.getText().toString();
            formattedString = formattedString + "FROM WEB" + "\n";
            try {
                data = new JSONObject(result);
                if (data.getString(code).equals("null"))
                    formattedString = formattedString + "Code not available in web" + "\n";
                else
                    formattedString = formattedString + code + ": " + data.getString(code) + "\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }

            formattedString = formattedString + "\n" + "FROM DATABASE" + "\n";
            Cursor c = db.rawQuery("SELECT * FROM " + tablename, null);
            if (c.getCount() != 0)
            {
                c.moveToFirst();
                for (int i=0; i<c.getCount(); i++){
                    if (c.getString(1).equals(code)){
                        formattedString = formattedString + c.getString(1) + "\n";
                    }
                    c.moveToNext();
                }
            }

        }
        return formattedString;
    }
}
