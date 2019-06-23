package com.android.tawjihi_api;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatsActivity extends AppCompatActivity {

    private static final String TAG = StatsActivity.class.getSimpleName();

    @BindView(R.id.frg_name)
    TextView frgName;
    @BindView(R.id.frg_school)
    TextView frgSchool;
    @BindView(R.id.frg_region)
    TextView frgRegion;
    @BindView(R.id.frg_score)
    TextView frgScore;
    @BindView(R.id.frg_branch_img)
    ImageView frgBranchImg;
    @BindView(R.id.frg_branch)
    TextView frgBranch;
    @BindView(R.id.frg_school_rank)
    TextView frgSchoolRank;
    @BindView(R.id.frg_region_rank)
    TextView frgRegionRank;
    @BindView(R.id.frg_overAll_rank)
    TextView frgOverAllRank;
    @BindView(R.id.frg_branch_rank)
    TextView frgBranchRank;
    @BindView(R.id.frg_toolbar)
    Toolbar toolbar;

    ResultInfo resultinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_fragment);
        ButterKnife.bind(this);
        resultinfo = (ResultInfo) getIntent().getSerializableExtra("ResultInfoObject");
        new OkHttpHandler().execute(resultinfo);

        setTitle(resultinfo.name);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        frgBranch.setText(resultinfo.branch);
        frgSchool.setText(resultinfo.school);
        frgScore.setText(resultinfo.score);
        frgRegion.setText(resultinfo.region);
        frgName.setText(resultinfo.name);

        if (resultinfo.score.isEmpty())
            resultinfo.score = "0";
        float _score = Float.parseFloat(resultinfo.score);

        if (_score > 90) // green
        {
            frgScore.setTextColor(ContextCompat.getColor(this, R.color.veryGood));
        } else if (_score > 80) // yellow
        {
            frgScore.setTextColor(ContextCompat.getColor(this, R.color.good));
        } else if (_score > 70)// orange
        {
            frgScore.setTextColor(ContextCompat.getColor(this, R.color.notBad));
        } else { // red
            frgScore.setTextColor(ContextCompat.getColor(this, R.color.fail));
        }

        switch (resultinfo.branch) {
        case "العلمي":
            frgBranchImg.setImageResource(R.drawable.ic_flask);
            break;

        case "الأدبي":
            frgBranchImg.setImageResource(R.drawable.ic_book);
            break;
        case "الادبي":
            frgBranchImg.setImageResource(R.drawable.ic_book);
            break;
        case "العلوم الإنسانية":
            frgBranchImg.setImageResource(R.drawable.ic_book);
            break;
        case "العلوم الانسانية":
            frgBranchImg.setImageResource(R.drawable.ic_book);
            break;

        case "الفندقي":
            frgBranchImg.setImageResource(R.drawable.ic_hotel);
            break;
        case "تطبيقي فندقي":
            frgBranchImg.setImageResource(R.drawable.ic_hotel);
            break;

        case "الزراعي":
            frgBranchImg.setImageResource(R.drawable.ic_planting);
            break;
        case "الزراعي التطبيقي":
            frgBranchImg.setImageResource(R.drawable.ic_planting);
            break;
        case "الزراعي المهني":
            frgBranchImg.setImageResource(R.drawable.ic_planting);
            break;
        case "تطبيقي زراعي":
            frgBranchImg.setImageResource(R.drawable.ic_planting);
            break;

        case "التجاري":
            frgBranchImg.setImageResource(R.drawable.ic_shop);
            break;
        case "الريادة والأعمال":
            frgBranchImg.setImageResource(R.drawable.ic_shop);
            break;
        case "تطبيقي اقتصاد منزلي":
            frgBranchImg.setImageResource(R.drawable.ic_shop);
            break;
        case "الاقتصاد المنزلي التطبيقي":
            frgBranchImg.setImageResource(R.drawable.ic_shop);
            break;
        case "الاقتصاد المنزلي":
            frgBranchImg.setImageResource(R.drawable.ic_shop);
            break;

        case "الصناعي":
            frgBranchImg.setImageResource(R.drawable.ic_tools);
            break;
        case "الصناعي التطبيقي":
            frgBranchImg.setImageResource(R.drawable.ic_tools);
            break;
        case "تطبيقي صناعي":
            frgBranchImg.setImageResource(R.drawable.ic_tools);
            break;

        case "الشرعي":
            frgBranchImg.setImageResource(R.drawable.ic_islam);
            break;

        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class OkHttpHandler extends AsyncTask<ResultInfo, String, String> {

        public OkHttpHandler() {

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(ResultInfo... params) {
            OkHttpClient client = new OkHttpClient();

            FormBody formBody = new FormBody.Builder().add("id", params[0].id).add("year", params[0].year)
                    .add("branch", params[0].branch).add("school", params[0].school).add("region", params[0].region)
                    .build();

            Request request = new Request.Builder().post(formBody).url(MainActivity.BASE_URL + MainActivity.STATS_ROUTE)
                    .build();

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
            Log.d(TAG, "onPostExecute: " + s);
            super.onPostExecute(s);
            if (s == null || s.length() == 0) {
                return;
            }
            Gson gson = new Gson();
            StatsInfo temp = null;
            try {
                temp = gson.fromJson(s, StatsInfo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (temp == null) {
                // Snackbar.make(null,
                // "No Result Found", Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "nothing found?", Toast.LENGTH_SHORT).show();
            } else {
                updateUI(temp);
            }
        }
    }

    class StatsInfo {
        String regionRank;
        String branchRank;
        String schoolRank;
        String overAllRank;
    }

    void updateUI(StatsInfo temp) {
        frgSchoolRank.setText(temp.schoolRank);
        frgBranchRank.setText(temp.branchRank);
        frgOverAllRank.setText(temp.overAllRank);
        frgRegionRank.setText(temp.regionRank);
        frgSchoolRank.setText(temp.schoolRank);
    }
}
