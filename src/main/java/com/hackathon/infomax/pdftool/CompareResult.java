package com.hackathon.infomax.pdftool;

import java.util.List;

public class CompareResult {
    private String id;
    private List<CompareFieldInfo> compareFieldInfos;

    public void setCompareFieldInfos(List<CompareFieldInfo> compareFieldInfos) {
        this.compareFieldInfos = compareFieldInfos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CompareFieldInfo> getCompareFieldInfos() {
        return compareFieldInfos;
    }
}
