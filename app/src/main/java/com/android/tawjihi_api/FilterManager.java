package com.android.tawjihi_api;

import java.util.ArrayList;

import okhttp3.FormBody;

public class FilterManager {

    ArrayList<String> years;
    ArrayList<String> branches;
    ArrayList<String> schools;
    ArrayList<String> regions;

    private static FilterManager mInstance = null;

    private FilterManager() {
        years = new ArrayList<>();
        branches = new ArrayList<>();
        schools = new ArrayList<>();
        regions = new ArrayList<>();
    }

    public static FilterManager getInstance() {
        if (mInstance == null) {
            mInstance = new FilterManager();
        }
        return mInstance;
    }

    public void addYear(String year) {
        // to fight duplicates..
        if (!mInstance.years.contains(year))
            mInstance.years.add(year);
    }

    public void removeYear(String year) {
        if (mInstance.years.contains(year))
            mInstance.years.remove(year);
    }

    public void addBranch(String branch) {
        if (!mInstance.branches.contains(branch))
            mInstance.branches.add(branch);
    }

    public void removeBranch(String Branch) {
        if (mInstance.years.contains(Branch))
            mInstance.years.remove(Branch);
    }

    public void addSchool(String school) {
        if (!mInstance.schools.contains(school))
            mInstance.schools.add(school);
    }

    public void removeSchool(String School) {
        if (mInstance.years.contains(School))
            mInstance.years.remove(School);
    }

    public void addRegion(String region) {
        if (!mInstance.regions.contains(region))
            mInstance.regions.add(region);
    }

    public void removeRegion(String region) {
        if (mInstance.years.contains(region))
            mInstance.years.remove(region);
    }

    public void resetFilters() {
        years.clear();
        branches.clear();
        schools.clear();
        regions.clear();
    }

    public boolean isBranchesEmpty() {
        return branches.isEmpty();
    }

    public boolean isSchoolsEmpty() {
        return schools.isEmpty();
    }

    public boolean isYearsEmpty() {
        return years.isEmpty();
    }

    public boolean isRegionsEmpty() {
        return regions.isEmpty();
    }

    public boolean isEmpty() {
        return ((this.isBranchesEmpty() && this.isSchoolsEmpty()) && (this.isYearsEmpty() && this.isRegionsEmpty()));
    }

    public static FormBody addFilters(final FormBody.Builder request_build) {

        if (mInstance == null)
            mInstance = new FilterManager();

        for (String obj : mInstance.branches)
            request_build.add("branch", obj);
        for (String obj : mInstance.schools)
            request_build.add("school", obj);
        for (String obj : mInstance.years)
            request_build.add("year", obj);
        for (String obj : mInstance.regions)
            request_build.add("region", obj);
        return request_build.build();
    }
}
