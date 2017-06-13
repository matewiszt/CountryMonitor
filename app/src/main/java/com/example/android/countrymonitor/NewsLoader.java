package com.example.android.countrymonitor;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    //Global variable for the URL
    private String mUrl;

    /*
     * Public constructor
     * @param context: context of the Loader
     * @param url: the requestUrl for the AsyncTask
     */

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /*
     * Force the loading on Start
     */

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /*
     * Fetch the data from the URL in the background thread
     * @return List<News>: list of the News objects
     */
    @Override
    public List<News> loadInBackground() {

        //If the URL is empty, don't do anything
        if (mUrl == null){
        return null;
        }

        //If there is a request URL, get the data from it and return
        List<News> news = Utils.fetchNewsData(mUrl);
        return news;
    }
}
