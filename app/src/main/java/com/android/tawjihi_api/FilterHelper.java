package com.android.tawjihi_api;

//public class FilterHelper {
//TRUE: filter it(dont display it)
// FALSE : dont filter it (display it)
//
//  HashMap<String, Boolean> years;
//  HashMap<String, Boolean> branches;
//  HashMap<String, Boolean> schools;
//  HashMap<String, Boolean> regions;
//
//  private static FilterHelper mInstance = null;
//
//  private FilterHelper() {
//    years = new HashMap<String, Boolean>();
//    branches = new HashMap<String, Boolean>();
//    schools = new HashMap<String, Boolean>();
//    Sregions = new HashMap<String, Boolean>();
//  }
//
//  public static FilterHelper getInstance() {
//    if (mInstance == null) {
//      mInstance = new FilterHelper();
//    }
//    return mInstance;
//  }
//
//  public static void filterYear(String year, Boolean status) {
//    mInstance.years.put(year, status);
//  }
//
//  public static void filterBranches(String branch, Boolean status) {
//    mInstance.branches.put(branch, status);
//  }
//
//  public static void filterSchools(String school, Boolean status) {
//    mInstance.schools.put(school, status);
//  }
//
//  public static void filterRegions(String region, Boolean status) {
//    mInstance.regions.put(region, status);
//  }
//
//  public static void clearSelections(){
//    mInstance.years.clear();
//    mInstance.branches.clear();
//    mInstance.schools.clear();
//    mInstance.regions.clear();
//  }
//
//  public static void updateAdapter(MyAdapter adapter) {
//    ArrayList<ResultInfo> FilteredResults = new ArrayList<>(5);
//    for (ResultInfo temp : adapter.getDataSet()) {
//      if (mInstance.years.get(temp.year) == null || mInstance.years.get(temp.year) == true) {
//        continue;
//      } else if (mInstance.branches.get(temp.branch) == null || mInstance.branches.get(temp.branch) == true) {
//        continue;
//      } else if (mInstance.schools.get(temp.school) == null || mInstance.schools.get(temp.school) == true) {
//        continue;
//      } else if (mInstance.regions.get(temp.region) == null || mInstance.regions.get(temp.region) == true) {
//        continue;
//      } else {
//        FilteredResults.add(temp);
//      }
//    }
//    MyAdapter.replaceList(FilteredResults);
//    adapter.notifyDataSetChanged();
//  }
//}
// endregion
public class FilterHelper {

  static class FilterBuilder {

    private int type;
    private String data;
    public static final int FILTER_TYPE_YEAR = 1;
    public static final int FILTER_TYPE_BRANCH = 2;
    public static final int FILTER_TYPE_SCHOOL = 3;
    public static final int FILTER_TYPE_REGION = 4;

    public FilterBuilder() {
    }

    public FilterBuilder setType(int filterType) {
      if (filterType > 0 && filterType < 4) {
        this.type = filterType;
      }
      return this;
    }

    public FilterBuilder setData(String data) {
      this.data = data;
      return this;
    }
  }

}