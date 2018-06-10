package com.hackathon.infomax.pdftool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompareEngine {
    private static final BigDecimal NUMBER_TOLERANCE = new BigDecimal("0.05");
    private static AnalyzeUtil analyzeUtil = new AnalyzeUtil();

    public List<CompareResult> doCompare(String templateHtmlFile, String tobeComparedHtmlFile) throws Exception {
        List<String> templateNodes = getNodes(templateHtmlFile);

        List<String> tobeComparedNodes = getNodes(tobeComparedHtmlFile);

        List<ValueMataData> templateValueMetaData = new ArrayList<>();
        templateNodes.forEach(t -> templateValueMetaData.add(analyzeUtil.getValueMetaData(t)));

        List<ValueMataData> tobeComparedValueMetaData = new ArrayList<>();
        tobeComparedNodes.forEach(t -> tobeComparedValueMetaData.add(analyzeUtil.getValueMetaData(t)));

        List<CompareResult> compareResultsTable = compareNodes(templateValueMetaData, tobeComparedValueMetaData);
        List<CompareResult> compareResultsParagraph = doCompareParagraph(templateHtmlFile, tobeComparedHtmlFile);

        compareResultsTable.addAll(compareResultsParagraph);

        return compareResultsTable;
    }


    public List<CompareResult> doCompareWords(String templateHtmlFile, String templateHtmlFile2) throws Exception {
        String contentF1 = analyzeUtil.readFileAsString(templateHtmlFile);
        Pattern pattern = Pattern.compile("<div.+id=\"(\\w+)\".+>(Includes.+)<\\/div>\\n.+<div.+>(.+)<\\/div>");
        Matcher matcher = pattern.matcher(contentF1);

        List<ValueMataData> f1List = new ArrayList<>();
        while (matcher.find()) {
            ValueMataData valueMataData = new ValueMataData();
            valueMataData.setId(matcher.group(1));
            valueMataData.setValue(matcher.group(2).concat(matcher.group(3)));
            valueMataData.setRawDom(matcher.group(0));
            f1List.add(valueMataData);
        }

        String contentF2 = analyzeUtil.readFileAsString(templateHtmlFile2);
        matcher = pattern.matcher(contentF2);
        List<ValueMataData> f2List = new ArrayList<>();
        while (matcher.find()) {
            ValueMataData valueMataData = new ValueMataData();
            valueMataData.setId(matcher.group(1));
            valueMataData.setValue(matcher.group(2).concat(matcher.group(3)));
            valueMataData.setRawDom(matcher.group(0));
            f2List.add(valueMataData);
        }

        List<CompareResult> compareResults = new ArrayList<>();
        for (int i = 0; i < f1List.size(); i++) {
            ValueMataData f1Con = f1List.get(i);
            ValueMataData f2Con = f2List.get(i);
            if (f1Con.getValue().equals(f2Con.getValue())) {
                continue;
            }

            Elements elements1 = Jsoup.parse(f1Con.getRawDom()).select("div");
            Elements elements2 = Jsoup.parse(f2Con.getRawDom()).select("div");
            for (int j = 0; j < elements1.size(); j++) {
                Element ele1 = elements1.get(j);
                Element ele2 = elements2.get(j);

                CompareResult cr = new CompareResult();

                List<CompareFieldInfo> list = new ArrayList<>();
                CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
                compareFieldInfo.setExpectedValue(ele1.text());
                compareFieldInfo.setActualValue(ele2.text());
                list.add(compareFieldInfo);
                cr.setCompareFieldInfos(list);
                cr.setId(ele1.attr("id"));
                compareResults.add(cr);
            }
        }

        return compareResults;
    }

    private List<String> getNodes(String htmlFile) throws Exception {
        analyzeUtil.extract(htmlFile);

        List<String> nodesInfo = new ArrayList<>();
        nodesInfo.addAll(analyzeUtil.findElementsByRegexp("<div.+id=\"p(225|226|227)\".+>([\\w\\,]+)<\\/div>", htmlFile));
        nodesInfo.addAll(analyzeUtil.findElementsByRegexp("<div.+id=\"p(228|229|230)\".+>([\\w\\,]+)<\\/div>", htmlFile));
        nodesInfo.addAll(analyzeUtil.findElementsByRegexp("<div.+id=\"p(231|232|233)\".+>([\\w\\,]+)<\\/div>", htmlFile));
        nodesInfo.addAll(analyzeUtil.findElementsByRegexp("<div.+id=\"p(234|235|236)\".+>([\\w\\,]+)<\\/div>", htmlFile));

        return nodesInfo;
    }

    private List<CompareResult> compareNodes(List<ValueMataData> templateValueMetaData, List<ValueMataData> tobeComparedValueMetaData) {
        List<CompareResult> compareResults = new ArrayList<>();

        for (int i = 0; i < templateValueMetaData.size(); i++) {
            ValueMataData vmdTemplate = templateValueMetaData.get(i);
            ValueMataData vmdTobeComp = tobeComparedValueMetaData.get(i);

            CompareResult compareResult = new CompareResult();
            compareResult.setId(vmdTemplate.getId());

            List<CompareFieldInfo> list = new ArrayList<>();
            if (vmdTobeComp.getValue().equals("fake")) {
                CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
                compareFieldInfo.setPropertyName("value-not-set");
                compareFieldInfo.setExpectedValue(vmdTemplate.getValue());
                compareFieldInfo.setActualValue("null");
                list.add(compareFieldInfo);
            } else if (!vmdTemplate.getFontFamily().equals(vmdTobeComp.getFontFamily())) {
                CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
                compareFieldInfo.setPropertyName("font-family");
                compareFieldInfo.setExpectedValue(vmdTemplate.getFontFamily());
                compareFieldInfo.setActualValue(vmdTobeComp.getFontFamily());
                list.add(compareFieldInfo);

            } else if (!vmdTemplate.getFontSize().equals(vmdTobeComp.getFontSize())) {
                CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
                compareFieldInfo.setPropertyName("font-size");
                compareFieldInfo.setExpectedValue(vmdTemplate.getFontSize());
                compareFieldInfo.setActualValue(vmdTobeComp.getFontSize());
                list.add(compareFieldInfo);
            } else if (vmdTemplate.isNumber() ^ vmdTobeComp.isNumber()) {
                CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
                compareFieldInfo.setPropertyName("is-number");
                compareFieldInfo.setExpectedValue(vmdTemplate.isNumber() ? "numeric" : "string");
                compareFieldInfo.setActualValue(vmdTobeComp.isNumber() ? "numeric" : "string");
                list.add(compareFieldInfo);
            } else if (vmdTemplate.isNumber()) {
                BigDecimal numberDecimal = new BigDecimal(vmdTemplate.getValue().replace(",", ""));
                BigDecimal ceiling = numberDecimal.add(numberDecimal.multiply(NUMBER_TOLERANCE));
                BigDecimal floor = numberDecimal.subtract(numberDecimal.multiply(NUMBER_TOLERANCE));

                BigDecimal tobeComparedValue = new BigDecimal(vmdTobeComp.getValue().replace(",", ""));
                if (tobeComparedValue.compareTo(ceiling) == 1 || tobeComparedValue.compareTo(floor) == -1) {
                    CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
                    compareFieldInfo.setPropertyName("number tolerance out of range");
                    compareFieldInfo.setExpectedValue(vmdTemplate.getValue());
                    compareFieldInfo.setActualValue(vmdTobeComp.getValue());
                    list.add(compareFieldInfo);
                }
            }

            if (!list.isEmpty()) {
                compareResult.setCompareFieldInfos(list);
                compareResults.add(compareResult);
            }
        }

        return compareResults;
    }

    private List<CompareResult> doCompareParagraph(String templateHtmlFile, String tobeComparedHtmlFile) throws Exception {
        List<String> list1 = analyzeUtil.findElementsByRegexp("<div.+Highlights<\\/div>(\\n.+)+reform.+<\\/div>", templateHtmlFile);
        Document document1 = Jsoup.parse(list1.get(0));
        Elements elements1 = document1.select("div");

        List<String> list2 = analyzeUtil.findElementsByRegexp("<div.+Highlights<\\/div>(\\n.+)+reform.+<\\/div>", tobeComparedHtmlFile);
        Document document2 = Jsoup.parse(list2.get(0));
        Elements elements2 = document2.select("div");

        int size1 = elements1.size();
        int size2 = elements2.size();
        if (size1 != size2) {
            int sizeDiff = Math.abs(size1 - size2);
            if (size1 > size2) {
                for (int i = 0; i < sizeDiff; i++)
                    elements2.add(new Element("<div class=\"p\" id=\"placeholderid\" style=\"top:260.315pt;left:57.0pt;line-height:16.755005pt;font-family:Arial;font-size:15.0pt;letter-spacing:-4.0E-4pt;width:65.870995pt;\">placeholder</div>"));
            } else {
                for (int i = 0; i < sizeDiff; i++)
                    elements1.add(new Element("<div class=\"p\" id=\"placeholderid\" style=\"top:260.315pt;left:57.0pt;line-height:16.755005pt;font-family:Arial;font-size:15.0pt;letter-spacing:-4.0E-4pt;width:65.870995pt;\">placeholder</div>"));
            }
        }

        List<ValueMataData> metaDataList1 = new ArrayList<>();
        List<ValueMataData> metaDataList2 = new ArrayList<>();
        for (int i = 0; i < elements1.size(); i++) {
            Element ele1 = elements1.get(i);
            Element ele2 = elements2.get(i);

            ValueMataData metaData1 = new ValueMataData();
            Pattern pattern = Pattern.compile("<div.+id=\"(\\w+)\".+font-family:(\\w+);font-size:([\\w\\.]+).+>(.+)<\\/div>");
            Matcher matcher = pattern.matcher(ele1.toString().replace("\n", ""));
            if (matcher.find()) {
                metaData1.setRawDom(matcher.group(0));
                metaData1.setId(matcher.group(1));
                metaData1.setFontFamily(matcher.group(2));
                metaData1.setFontSize(matcher.group(3));
                metaData1.setValue(matcher.group(4));
                metaDataList1.add(metaData1);
            }

            ValueMataData metaData2 = new ValueMataData();
            matcher = pattern.matcher(ele2.toString().replace("\n", ""));
            if (matcher.find()) {
                metaData2.setRawDom(matcher.group(0));
                metaData2.setId(matcher.group(1));
                metaData2.setFontFamily(matcher.group(2));
                metaData2.setFontSize(matcher.group(3));
                metaData2.setValue(matcher.group(4));
                metaDataList2.add(metaData2);
            }
        }

        List<CompareResult> compareResultsParagraph = new ArrayList<>();
        for (int i = 0; i < metaDataList1.size(); i++) {
            ValueMataData f1Con = metaDataList1.get(i);
            ValueMataData f2Con = metaDataList2.get(i);
            if (f1Con.getValue().equals(f2Con.getValue())) {
                continue;
            }

            CompareResult cr = new CompareResult();

            cr.setId(f1Con.getId());
            List<CompareFieldInfo> list = new ArrayList<>();
            CompareFieldInfo compareFieldInfo = new CompareFieldInfo();
            compareFieldInfo.setExpectedValue(f1Con.getValue());
            compareFieldInfo.setActualValue(f2Con.getValue());
            list.add(compareFieldInfo);
            cr.setCompareFieldInfos(list);

            compareResultsParagraph.add(cr);
        }

        return compareResultsParagraph;
    }
}
