package com.android.tawjihi_api;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class mySearchActivity extends ListActivity {
  public static String _URL = "http://localhost:3000";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Get the intent, verify the action and get the query
    Intent intent = getIntent();
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      query = _URL + query;
      doMySearch(query);
    }
  }

  private String doMySearch(String query) {
    Log.d("FROM mySerachActivity", "doMySearch: " + query);
    // String data = null;
    // try {
    // data = new getData().execute(query).get();
    // }catch(Exception e){}
    // return data;
    return "hello";
  }
}
