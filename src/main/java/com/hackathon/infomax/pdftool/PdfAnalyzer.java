package com.hackathon.infomax.pdftool;

import java.util.List;

public class PdfAnalyzer {

    public static void main(String[] args) throws Exception {
        CompareEngine compareEngine = new CompareEngine();

        System.out.println("---------------- table compare result ---------------- ");
        List<CompareResult> compareResults = compareEngine.doCompare("informax.pdf", "informax-wrong.pdf");
        compareResults.forEach(PdfAnalyzer::dumpResult);

//        System.out.println("---------------- word compare result ---------------- ");
//        compareResults = compareEngine.doCompareWords("1.pdf", "2.pdf");
//        compareResults.forEach(PdfAnalyzer::dumpResult);
    }

    private static void dumpResult(CompareResult compareResult) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("id:").append(compareResult.getId()).append("\n");
        compareResult.getCompareFieldInfos().forEach(obj -> stringBuffer.append(dumpFieldInfo(obj)));
        System.out.println(stringBuffer.toString());
    }

    private static String dumpFieldInfo(CompareFieldInfo compareFieldInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("propertyName:").append(compareFieldInfo.getPropertyName())
                .append("\nexpected Value:").append(compareFieldInfo.getExpectedValue())
                .append("\nactualValue:").append(compareFieldInfo.getActualValue());

        return stringBuffer.toString();
    }
}