package com.hackathon.infomax.domain;

import lombok.Data;

@Data
public class ResultVO {
    private Integer id;
    private String fundName="";
    private String month="";
    private Integer diff=0;
    private String link;


    public Integer getId() {
        return id;
    }

    public String getFundName() {
        return fundName;
    }

    public String getMonth() {
        return month;
    }

    public Integer getDiff() {
        return diff;
    }

    public String getLink() {
        return link;
    }

}
