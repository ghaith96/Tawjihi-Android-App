package com.android.tawjihi_api;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

// one master listener handler, with switch statements for different views
// use butterknife to bind views and methods
// convert all the filtering into api, using the same endpoint search
// add compression capabilities to the server
// long press FAB clear all filters and re-request the original data from api if searchField is not empty
// add new endpoints for searching separately by school/branch/region if the search field is empty
// implements the search as per instructions on the official android website, using intents and services
// convert database into not full-text-search, using external content tables
// stats Activity, rank over year,branch,region,school

/* 22/6 */

//TODO: implement an indicator to notify the user when using filters
//TODO: convert dialogs into my_custom_dialog class
//TODO: optimize the FAB click dialog, maybe create it in seperate thread at the start then show it when needed !
//TODO: implement some kind of tutorial/docs ***4fun4***

/*NEW*/
//TODO: use one static debug key
//TODO: save the last 3 queries as comma separated string using sharedPreferences
//TODO: parse remaining data
//TODO: pro guard learning
//TODO: better style

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static String BASE_URL = "http://localhost:3000";
    public static final String ROUTE = "/search";
    public static final String HINT_ROUTE = "/getHints";
    public static final String STATS_ROUTE = "/stats";

    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ResultInfo> mDataset;
    SearchView searchView;
    Dialog dialog;
    HintsManager hintsManager;
    FilterManager filterManager;
    Snackbar undoSnackBar;

    @BindView(R.id.tb_toolbarsearch)
    Toolbar tbMainSearch;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.my_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.mainthingy)
    CoordinatorLayout mainthingy;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;

    // region Android LifeCycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dialog = new Dialog(MainActivity.this);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };

        // region mRecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.getItemAnimator();
        mRecyclerView.setHasFixedSize(true);

        // endregion

        mDataset = new ArrayList<>(0);
        mAdapter = new MyAdapter(mDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        tbMainSearch.setTextDirection(View.TEXT_DIRECTION_RTL);

        tbMainSearch.setTitle("");

        hintsManager = new HintsManager();
        filterManager = FilterManager.getInstance();

        setSupportActionBar(tbMainSearch);

        fab.setOnClickListener(this);

        if (hintsManager.isEmpty())
            initializeHints();

        undoSnackBar = Snackbar.make(mainthingy, "تم مسح الفلاتر", Snackbar.LENGTH_SHORT)
                .setActionTextColor(Color.GREEN).setAction("UNDO", this).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        if (event == DISMISS_EVENT_TIMEOUT)
                            doFilterReset();
                    }
                });
        ViewCompat.setLayoutDirection(undoSnackBar.getView(), ViewCompat.LAYOUT_DIRECTION_RTL);
        super.onCreate(savedInstanceState);

        // ActionBar actionBar = getSupportActionBar();
        // actionBar.setHomeAsUpIndicator(R.drawable.my_clear);
        // actionBar.setDisplayHomeAsUpEnabled(true);

        // try{
        // run("احمد محمود محمد");
        // }catch (IOException e){e.printStackTrace();}
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mShimmerViewContainer.stopShimmer();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: shared preferences
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query.equals("") && filterManager.isEmpty())
                return;
            new OkHttpHandler().execute(query);
        }
    }

    // endregion methods

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            mDataset.clear();
            mAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem mSearchMenuItem = menu.findItem(R.id.menu_toolbarsearch);
        searchView = (SearchView) mSearchMenuItem.getActionView();
        searchView.setMaxWidth(android.R.attr.maxWidth);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryHint("ادخل اسم للبحث..");
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private class OkHttpHandler extends AsyncTask<String, String, String> {

        // TODO: deprecated ProgressDialog, find alternative
        // private ProgressDialog dialog;

        public OkHttpHandler() {
            if (mDataset != null && mDataset.size() > 0) {
                mDataset.clear();
            }
            // dialog = new ProgressDialog(MainActivity.this);
            // dialog.setCanceledOnTouchOutside(false);

        }

        @Override
        protected void onPreExecute() {
            // dialog.setMessage("Searching, hold on..");
            // dialog.show();
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmer();

        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

            FormBody.Builder request_build = new FormBody.Builder().add("name", params[0]);

            FormBody formBody = FilterManager.addFilters(request_build);

            Request request = new Request.Builder().post(formBody).url(BASE_URL + ROUTE).build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // if (dialog.isShowing()) {
            // dialog.dismiss();
            // }
            if (s == null || s.length() == 0) {
                return;
            }
            Gson gson = new Gson();
            Log.d(TAG, "onPostExecute: " + s);
            ResultInfo[] temp = null;
            try {
                temp = gson.fromJson(s, ResultInfo[].class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (temp == null || temp.length <= 0) {
                Snackbar.make(findViewById(R.id.mainthingy), "No Result Found", Snackbar.LENGTH_SHORT).show();
            }

            mDataset.addAll(Arrays.asList(temp));
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            mAdapter.notifyDataSetChanged();
        }
    }

    // region onQueryListener methods
    @Override
    public boolean onQueryTextSubmit(String query) {
        Intent intent = new Intent(searchView.getContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        startActivity(intent);
        // if (query.equals("0000")) {
        // changeURL();
        // } else {
        // new OkHttpHandler().execute(query);
        // SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
        // MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        // suggestions.saveRecentQuery(query, null);
        // }
        tbMainSearch.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
    // endregion

    // region Listeners (Click,LongClick)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.fab:
            handleFabClick();
            break;
        case R.id.clear:
            handleClearClick();
            break;
        case R.id.submit:
            handleSubmitClick();
            break;
        }
    }

    public void handleSubmitClick() {
        dialog.dismiss();

        final NachoTextView nacho_branch = dialog.findViewById(R.id.nacho_branch);
        final NachoTextView nacho_region = dialog.findViewById(R.id.nacho_region);
        final NachoTextView nacho_school = dialog.findViewById(R.id.nacho_school);
        final NachoTextView nacho_year = dialog.findViewById(R.id.nacho_year);

        // TODO: populate hintsManager from api, OnCreate? async?
        if (hintsManager.isEmpty())
            initializeHints();

        nacho_branch.chipifyAllUnterminatedTokens();
        nacho_school.chipifyAllUnterminatedTokens();
        nacho_region.chipifyAllUnterminatedTokens();
        nacho_year.chipifyAllUnterminatedTokens();

        // region update the FilterManager

        // first reset
        filterManager.resetFilters();

        // then insert up-to-date
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                filterManager.schools.addAll(nacho_school.getChipValues());
                filterManager.regions.addAll(nacho_region.getChipValues());
                filterManager.years.addAll(nacho_year.getChipValues());
                filterManager.branches.addAll(nacho_branch.getChipValues());
            }
        });
        t1.start();
        // endregion region

        // re-request latest saved query from api depends on the filters
        // searchView.setQuery(searchView.getQuery(), true); this wont work if the query
        // is empty!

        // but manual search works even if the query is empty, this is so we can search
        // using different filters without any name !
        // new OkHttpHandler().execute(searchView.getQuery().toString());

        Intent intent = new Intent(searchView.getContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, searchView.getQuery().toString());
        startActivity(intent);
    }

    public void handleClearClick() {
        NachoTextView nacho_branch = dialog.findViewById(R.id.nacho_branch);
        NachoTextView nacho_region = dialog.findViewById(R.id.nacho_region);
        NachoTextView nacho_school = dialog.findViewById(R.id.nacho_school);
        NachoTextView nacho_year = dialog.findViewById(R.id.nacho_year);

        nacho_branch.setText("");
        nacho_region.setText("");
        nacho_year.setText("");
        nacho_school.setText("");
        // TODO: re-request query from api after cleaning instead of leaving it empty
        mDataset.clear();
        mAdapter.notifyDataSetChanged();
        filterManager.resetFilters();
    }

    @OnClick(R.id.fab)
    public void handleFabClick() {

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.filter_dialog_layout);

        final NachoTextView nacho_branch = dialog.findViewById(R.id.nacho_branch);
        final NachoTextView nacho_region = dialog.findViewById(R.id.nacho_region);
        final NachoTextView nacho_school = dialog.findViewById(R.id.nacho_school);
        final NachoTextView nacho_year = dialog.findViewById(R.id.nacho_year);
        nacho_branch.setOnChipClickListener(new NachoTextView.OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip, MotionEvent event) {
                nacho_branch.enableEditChipOnTouch(false, false);
            }
        });
        if (hintsManager != null) {
            ArrayAdapter<String> branch_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                    hintsManager.branch);
            ArrayAdapter<String> school_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                    hintsManager.school);
            ArrayAdapter<String> region_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                    hintsManager.region);
            ArrayAdapter<String> year_adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                    hintsManager.year);
            nacho_branch.setAdapter(branch_adapter);
            nacho_region.setAdapter(region_adapter);
            nacho_school.setAdapter(school_adapter);
            nacho_year.setAdapter(year_adapter);
        }
        // TODO: initialize hintsManager from api, async maybe? only if not empty
        // nacho_branch.setAdapter(hint_branch);
        // nacho_region.setAdapter(hint_region);
        // nacho_school.setAdapter(hint_school);
        // nacho_year.setAdapter(hint_year);

        // press enter to form a chip
        nacho_branch.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        nacho_region.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        nacho_school.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        nacho_year.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);

        Window window = dialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        Button submit = dialog.findViewById(R.id.submit);

        if (!filterManager.isBranchesEmpty()) {
            nacho_branch.setText(filterManager.branches);
        }

        if (!filterManager.isYearsEmpty()) {
            nacho_year.setText(filterManager.years);
        }

        if (!filterManager.isRegionsEmpty()) {
            nacho_region.setText(filterManager.regions);
        }

        if (!filterManager.isSchoolsEmpty()) {
            nacho_school.setText(filterManager.schools);
        }

        Button clear = dialog.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        submit.setOnClickListener(this);
        dialog.show();
    }

    @OnLongClick(R.id.fab)
    public boolean handleFabLongClick() {
        // disable fab click and long click, to prevent unpredicted behaviour
        // re-enabled again in undoSnackBar callback and in doResetFilter()
        if (undoSnackBar != null)
            undoSnackBar.show();

        return true;
    }

    // endregion

    // region auxiliary functoins

    private void initializeHints() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(BASE_URL + HINT_ROUTE).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    // Log.d(TAG, "onResponse: " + responseBody.string());
                    Gson gson = new Gson();
                    hintsManager = gson.fromJson(responseBody.string(), HintsManager.class);

                }
            }
        });
    }

    public void doFilterReset() {
        mDataset.clear();
        filterManager.resetFilters();
        // searchView.setQuery(searchView.getQuery(), true);
        Intent intent = new Intent(searchView.getContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, searchView.getQuery().toString());
        startActivity(intent);
    }

    // endregion

    // region Development methods
    private void changeURL() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter new URL");
        final EditText input = new EditText(this);
        input.setText(BASE_URL);
        input.selectAll();
        input.setSelectAllOnFocus(true);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BASE_URL = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    // endregion

    // region temp/test
    // sync okhttp
    // void run(String name) throws IOException {
    //
    // OkHttpClient client = new OkHttpClient();
    // Request request = new Request.Builder()
    // .url(BASE_URL + name)
    // .build();
    //
    // client.newCall(request).enqueue(new Callback() {
    // @Override
    // public void onFailure(Call call, IOException e) {
    // call.cancel();
    // }
    //
    // @Override
    // public void onResponse(Call call, Response response) throws IOException {
    // final String myResponse = response.body().string();
    // MainActivity.this.runOnUiThread(new Runnable() {
    // @Override
    // public void run() {
    // Log.d(TAG, "run: " + myResponse);
    // output.setText(myResponse);
    // }
    // });
    //
    // }
    // });
    // }
    // endregion

}
