package com.example.android.countrymonitor;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {

    //Create a constant label for the log messages
    public static final String LOG_TAG = MainActivity.class.getName();

    //Create a variable for the NewsAdapter
    public NewsAdapter mAdapter;

    public static final String REQUEST_URL = "http://content.guardianapis.com/";

    //Create a TextView for the empty state view
    public TextView mEmptyStateTextView;

    //Create a ProgressBar to display before the loading is finished
    public ProgressBar progressBar;

    //Global variable for indication if the search is based on section or country
    public static int IS_SECTION_SEARCH = 0;

    //The queried section
    public String mSectionToMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //If the bundle is not empty (if a section was clicked), get the section and the section search constant value
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            IS_SECTION_SEARCH = bundle.getInt("IS_SECTION_SEARCH");
            mSectionToMonitor = bundle.getString("sectionToMonitor");
        }

        //Get the ListView for the list
        ListView listView = (ListView) findViewById(R.id.list);

        //Get the TextView with the ID empty_view and set it as an empty view
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        //Get the ProgressBar with the ID progress_bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        //Create a NewsAdapter for the list and set it
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(mAdapter);

        //Create a ConnectivityManager to get network info
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get network info and create a boolean variable to say if the device is connected to the internet or not
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //If the device is connected to the internet, initialize a Loader
        if (isConnected) {

            //Initialize a Loader
            getLoaderManager().initLoader(IS_SECTION_SEARCH, null, this);

        } else {

            //If there is no internet connection, hide the ProgressBar, set the text of the empty view
            // and print the error to the log
            progressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_connection_string);
            Log.e(LOG_TAG, getString(R.string.no_connection_string));
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {

        //Get the country indicated by the user in the Preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String countryToMonitor = sharedPrefs.getString(getString(R.string.settings_country_key),getString(R.string.settings_country_default)).toLowerCase().replace(" ", "");

        //Create a Uri and a UriBuilder
        Uri baseUri;
        Uri.Builder uriBuilder = new Uri.Builder();

        if (id == 0) {

            //If it is not section search, build the request URL from the country indicated by the user in the Preferences
            baseUri = Uri.parse(REQUEST_URL + "search");
            uriBuilder = baseUri.buildUpon();
            uriBuilder.appendQueryParameter("q", countryToMonitor);
        }

        if (id == 1){

            //If it is section search, build the request URL from the clicked section name
            String sectionToMonitor = mSectionToMonitor.toLowerCase().replace(" ", "");
            baseUri = Uri.parse(REQUEST_URL + sectionToMonitor);
            uriBuilder = baseUri.buildUpon();
        }

        //Append the API key to the request URL
        uriBuilder.appendQueryParameter("api-key", "test");

        //Create a NewsLoader with the request URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        //If the load has finished, hide the ProgressBar and set the empty view text to no result found string
        progressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_result_string);

        //Clear the Adapter
        mAdapter.clear();

        //If the List is not empty, add the List to the Adapter when the load is finished
        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }

        //Set the section search constant back to default
        IS_SECTION_SEARCH = 0;

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

        //Clear the Adapter when reseting the Loader
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate the menu with menu.xml
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Create an Intent to open the Settings page when the settings icon is clicked
        if (id == R.id.action_settings){
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
