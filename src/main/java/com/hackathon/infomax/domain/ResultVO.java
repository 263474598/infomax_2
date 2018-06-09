package com.hackathon.infomax.domain;

import lombok.Data;

@Data
public class ResultVO {
    private Integer id;
    private String fundName="";
    private String month="";
    private Integer diff=0;
    private String link;

}
