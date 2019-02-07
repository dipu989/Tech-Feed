package com.example.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles = new ArrayList<>();

    ArrayAdapter arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        try{
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,titles);

        listView.setAdapter(arrayAdapter);



    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            String result = "";
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();

                while(data!=-1)
                {
                    char current = (char)data;

                    result += current;

                    data = inputStreamReader.read();
                }

                JSONArray jsonArray = new JSONArray(result);

                int numberOfItems = 20;

                if(jsonArray.length() < 20)
                {
                    numberOfItems = jsonArray.length();
                }

                for(int i=0;i<numberOfItems;i++)
                {
                    String articleId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/"+ articleId +".json?print=pretty");
                    urlConnection = (HttpURLConnection)url.openConnection();
                    inputStream = urlConnection.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream);

                    data = inputStreamReader.read();

                    String articleInfo = "";
                    while(data!=-1)
                    {
                        char current = (char)data;
                        articleInfo += current;
                        data = inputStreamReader.read();
                    }

                    JSONObject jsonObject = new JSONObject(articleInfo);

                    if(!jsonObject.isNull("title") && !jsonObject.isNull("url")){
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");

                        url = new URL(articleUrl);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        inputStream = urlConnection.getInputStream();
                        inputStreamReader = new InputStreamReader(inputStream);
                        data = inputStreamReader.read();
                        String articleContent = "";
                        while(data!=-1)
                        {
                            char current = (char)data;
                            articleContent+= current;
                            data = inputStreamReader.read();
                        }
                        Log.i("Article Info ",articleContent);
                    }
                }

                Log.i("URL Content ",result);
                return result;



            }catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }
}
