package com.hackathon.infomax.controller;

import com.hackathon.infomax.domain.ResultVO;
import com.hackathon.infomax.mail.SendMail;
import com.hackathon.infomax.pdftool.AnalyzeUtil;
import com.hackathon.infomax.pdftool.CompareEngine;
import com.hackathon.infomax.pdftool.CompareResult;
import com.hackathon.infomax.util.FileUtility4MAX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pdf")
@Slf4j
public class PDFController {

    private final String classPDFPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "static/pdf/";

    @GetMapping("/getDiffDetails")
    @ResponseBody
    public List<CompareResult> getDiffDetails(String path, String pdfHtmlName) {

        Long current = Long.parseLong(path);
        Long original = current - 1;

        CompareEngine compareEngine = new CompareEngine();
        List<CompareResult> CompareResultList = null;
        try {
            if(pdfHtmlName.contains("AppleCorp")) {
                CompareResultList = compareEngine.doCompareWords(classPDFPath + current + "/" + pdfHtmlName, classPDFPath + original + "/" + pdfHtmlName);
            } else {
                CompareResultList = compareEngine.doCompare(classPDFPath + current + "/" + pdfHtmlName, classPDFPath + original + "/" + pdfHtmlName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return CompareResultList;
    }

    @GetMapping("/temp")
    public String temp(@RequestParam("tempPath") String tempPath, Model model) throws Exception {
        log.info("[PDFController] tempPath: {}", tempPath);
        model.addAttribute("tempPath", tempPath);
        return "temp";
    }

    @RequestMapping("/upload")
    @ResponseBody
    public String pdfUpload(String tempPath, HttpServletRequest request) throws Exception {

        List<MultipartFile> fileList = ((MultipartHttpServletRequest) request).getFiles("pdfFile");
//        String[] pdfHtmls = new String[10];

        for (MultipartFile file : fileList) {

            if (file.getSize() != 0) {
                //save path
//            Date date4Now = new Date();
//            String str4Now = new SimpleDateFormat("yyyyMMdd").format(date4Now);
                String pdfPath = classPDFPath + tempPath + "/" ;
                File pdfFilePath = new File(pdfPath);
                if (!pdfFilePath.exists()) {
                    boolean flag = pdfFilePath.mkdir();
                    log.info("[PDFController]{}", "Folder is created successfully: " + flag);
                }
                //Original File name
                String originFileName = file.getOriginalFilename();
                //New File name
//                String newFileName = UUID.randomUUID() + originFileName.substring(originFileName.lastIndexOf("."));
                //new File
                File newFile = new File(pdfPath + originFileName);
                //write File
                try {
                    file.transferTo(newFile);
                } catch (IOException e) {
                    log.error("[UploadException]{}", "IOException");
                }
            }
        }

        //Generate PDF file html
        List<String> pdfFileNameList = FileUtility4MAX.listPDF(classPDFPath + tempPath + "/");
        AnalyzeUtil analyzeUtil = new AnalyzeUtil();
        for(String pdfFileName : pdfFileNameList) {
            analyzeUtil.generate(classPDFPath + tempPath + "/", pdfFileName, classPDFPath + tempPath + "/");
        }

        //Delete pdf files
        FileUtility4MAX.deleteFile(classPDFPath + tempPath + "/");

        //Aggregate jsp

        return "0";
    }

    @RequestMapping("/compareList")
    @ResponseBody
    public List<ResultVO> compareList(String targetDate) {
        CompareEngine compareEngine = new CompareEngine();
        Long original = Long.parseLong(targetDate) - 1;
        List<String> htmlOriginalList = FileUtility4MAX.listHTML(classPDFPath + original + "/");
        List<String> htmlTargetList= FileUtility4MAX.listHTML(classPDFPath + targetDate + "/");

        List<ResultVO> resultVOList = new ArrayList<>();

        for(String html : htmlTargetList) {
            if(htmlOriginalList.contains(html)) {
                List<CompareResult> compareResultList = null;
                try {
                    if(html.contains("AppleCorp")) {
                        compareResultList = compareEngine.doCompareWords(classPDFPath + targetDate + "/"+html, classPDFPath + original + "/"+html);
                    } else {
                        compareResultList = compareEngine.doCompare(classPDFPath + targetDate + "/"+html, classPDFPath + original + "/"+html);
                    }

                    ResultVO resultVO = new ResultVO();
                    resultVO.setDiff(compareResultList.size());
                    resultVO.setMonth(targetDate);
                    resultVO.setFundName(html.substring(0, html.lastIndexOf(".")));
                    resultVO.setLink("<a href='http://localhost:8888/pdf/temp?tempPath=" + resultVO.getMonth() + "/" + html + "'  target='_blank'>" + "/" + resultVO.getMonth() + "/" + resultVO.getFundName() + "</a>");
                    resultVOList.add(resultVO);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ResultVO resultVO = new ResultVO();
                resultVO.setDiff(-1);
                resultVO.setMonth(targetDate);
                resultVO.setFundName(html.substring(0, html.lastIndexOf(".")));
                resultVO.setLink("");
                resultVOList.add(resultVO);
            }
        }

        SendMail sendMail = new SendMail();
        sendMail.sendMessage(resultVOList);
        return resultVOList;
    }

}
