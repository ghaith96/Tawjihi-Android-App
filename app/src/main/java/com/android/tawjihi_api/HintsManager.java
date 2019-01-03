package com.android.tawjihi_api;

import java.util.ArrayList;

public class HintsManager {
    ArrayList<String> school = new ArrayList<String>();
    ArrayList<String> region = new ArrayList<String>();
    ArrayList<String> year = new ArrayList<String>();
    ArrayList<String> branch = new ArrayList<String>();

    public boolean isEmpty() {
        return ((school.isEmpty() && region.isEmpty()) && (year.isEmpty() && branch.isEmpty()));
    }
}
