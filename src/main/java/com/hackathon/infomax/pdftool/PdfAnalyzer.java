package com.hackathon.infomax.pdftool;

import java.util.List;

public class PdfAnalyzer {

    public static void main(String[] args) throws Exception {
        AnalyzeUtil analyzeUtil = new AnalyzeUtil();
        analyzeUtil.generate("/Users/qiuguanglin/Downloads/", "informax.pdf", "/Users/qiuguanglin/Downloads");
        analyzeUtil.generate("/Users/qiuguanglin/Downloads/", "informax-wrong.pdf", "/Users/qiuguanglin/Downloads");

        CompareEngine compareEngine = new CompareEngine();
        System.out.println("---------------- table compare result ---------------- ");
        List<CompareResult> compareResults =
                compareEngine.doCompare("/Users/qiuguanglin/Downloads/informax.html", "/Users/qiuguanglin/Downloads/informax-wrong.html");
        compareResults.forEach(PdfAnalyzer::dumpResult);

        System.out.println("---------------- word compare result ---------------- ");
        analyzeUtil.generate("/Users/qiuguanglin/Downloads/", "1.pdf", "/Users/qiuguanglin/Downloads");
        analyzeUtil.generate("/Users/qiuguanglin/Downloads/", "2.pdf", "/Users/qiuguanglin/Downloads");

        compareEngine = new CompareEngine();
        System.out.println("---------------- table compare result ---------------- ");
        List<CompareResult> compareResults2 =
                compareEngine.doCompareWords("/Users/qiuguanglin/Downloads/1.html", "/Users/qiuguanglin/Downloads/2.html");
        compareResults2.forEach(PdfAnalyzer::dumpResult);
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