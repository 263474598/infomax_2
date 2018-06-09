package com.hackathon.infomax.pdftool;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AnalyzeUtil {
    private static final String FIEL_MATCH_REGEXP = "<div.+?id\\=\\\"(\\w+)\\\".+font-family:(\\w+).+font-size:(\\d+\\s?\\d*).+>([\\w,]+)<\\/div>";

    /**
     * generate a html from the pdf
     */
    public String generate(String path, String pdfFileName, String savePath) throws Exception {
//        PDDocument pdDocument = PDDocument.load(new File(getClass().getResource(pdfFileName).getPath()));
        PDDocument pdDocument = PDDocument.load(new File(path + pdfFileName));
        String htmlFileName = pdfFileName.substring(0, pdfFileName.lastIndexOf("."));
        new PDFDomTree().writeText(pdDocument, new FileWriter(savePath + "/" + htmlFileName.concat(".html")));

        return htmlFileName;
    }

    /**
     * print the content
     */
    void extract(String fileName) throws Exception {
        Document doc = Jsoup.parse(new File(fileName), "UTF-8");
        Elements pages = doc.select("div[class=page]");
        System.out.println(fileName + " : " + pages.size() + " pages totally");
    }


    /**
     * loop inside the nodes
     */
    private void analyzeNodeRecur(Element parentElement) {
        Elements elements = getAllDivImg(parentElement);
        List<Element> filteredElements = doFilter(elements);
        elements.clear();
        elements.addAll(filteredElements);

        for (int i = 1; i < elements.size(); i++) {
            Element element = elements.get(i);

            if (getAllDivImg(element).size() > 0) {
                analyzeNodeRecur(element);
            }
        }
    }


    public String readFileAsString(String fileName) throws Exception {
        String str;
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        while ((str = bufferedReader.readLine()) != null) {
            stringBuffer.append(str).append("\n");
        }

        return stringBuffer.toString();
    }

    ValueMataData getValueMetaData(String domText) {
        Pattern pattern = Pattern.compile(FIEL_MATCH_REGEXP);
        Matcher matcher = pattern.matcher(domText);

        if (matcher.find()) {
            ValueMataData valueMataData = new ValueMataData();

            valueMataData.setRawDom(matcher.group(0));
            valueMataData.setId(matcher.group(1));
            valueMataData.setFontFamily(matcher.group(2));
            valueMataData.setFontSize(matcher.group(3));
            valueMataData.setValue(matcher.group(4));
            valueMataData.setNumber(matcher.group(4).matches("^\\d+(,\\d{3})*?$"));

            return valueMataData;
        }

        return null;
    }

    /**
     * only care about those <div> and <img> nodes
     */
    private Elements getAllDivImg(Element parentElement) {
        Elements elements = new Elements();

        if (parentElement.children().size() == 0) {
            return elements;
        }

        Elements elementsDiv = parentElement.select("div");
        Elements elementsImg = parentElement.select("img");
        elements.addAll(elementsDiv);
        elements.addAll(elementsImg);
        return elements;
    }

    /**
     * filter the nodes content of empty and space
     */
    private List<Element> doFilter(Elements elements) {
        return elements.stream().filter(element -> {
            if (element.tagName().contains("img")) return true;

            if (element.childNodeSize() == 1) {
                Node node = element.childNode(0);
                if (node.nodeName().equals("#text") && !node.toString().contains("nbsp")) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    List<String> findElementsByRegexp(String exp, String fileName) throws Exception {
        List<String> foundList = new ArrayList<>();
        Pattern pattern = Pattern.compile(exp);
        Matcher matcher = pattern.matcher(readFileAsString(fileName));

        while (matcher.find()) {
            foundList.add(matcher.group(0));
        }

        return foundList;
    }
}
