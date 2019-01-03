package com.android.tawjihi_api;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class getData {

  private final OkHttpClient client = new OkHttpClient();
  private static getData instance = null;
  public static String _URL = "http://localhost:3000";

  private getData() {
  }

  public static getData getInstance() {
    if (instance == null)
      instance = new getData();
    return instance;
  }

  public String search(String name) {
    HttpUrl.Builder urlBuilder = HttpUrl.parse(_URL).newBuilder();
    urlBuilder.addQueryParameter("name", name);
    String url = urlBuilder.build().toString();
    Request request = new Request.Builder().url(url).build();
    try {
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
