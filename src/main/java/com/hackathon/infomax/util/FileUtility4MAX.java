package com.hackathon.infomax.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileUtility4MAX {

    public static List<String> listPDF(String path) {
        return listFile(path, "pdf");
    }

    public static List<String> listHTML(String path) {
        return listFile(path, "html");
    }

    public static List<String> listFile(String path, String endWith) {
        File dir = new File(path);
        List<String> pdfFileNames = new ArrayList<>();

        String[] names = dir.list();

        if(names!=null) {
            for (String name : names) {
                if(name.endsWith(endWith.toLowerCase()) || name.endsWith(endWith.toUpperCase())) {
                    pdfFileNames.add(name);
                }
            }
        }

        return pdfFileNames;
    }

    public static boolean deleteFile(String delpath) {
        File file = new File(delpath);
        if (!file.isDirectory()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                File delfile = fileList[i];
                if (!delfile.isDirectory()) {
                    if(delfile.getName().endsWith("pdf") || delfile.getName().endsWith("PDF")) {
                        int tryCount = 0;
                        boolean result=delfile.delete();
                        while (!result && tryCount++ < 10) {
                            System.gc();    //回收资源
                            result = delfile.delete();
                        }
                        log.info("Delete " + delfile.getName() + " successfully.");
                    }
                } else if (delfile.isDirectory()) {
                    deleteFile(fileList[i].getPath());
                }
            }
            file.delete();
        }
        return true;
    }

    public static void main(String[] args) {
        deleteFile("D:\\IntelliJ-workspace\\infomax_2\\target\\classes\\static\\pdf\\20180609");
    }

}
