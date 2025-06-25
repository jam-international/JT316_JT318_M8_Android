package com.jam_int.jt316_jt318_m8;

import android.content.Context;
import android.content.Intent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public static String Downcase_ExtensionFile(File destinationLocation) {

        String filename = destinationLocation.getAbsolutePath();

        String filename_less_estension = SubString.SubstringExtensions.BeforeLast(filename, ".");
        String estensione =  SubString.SubstringExtensions.After(filename, ".");
        String ext_downcase = estensione.toLowerCase();

        File destFile = new File(filename_less_estension+"."+ext_downcase);

        if (destinationLocation.renameTo(destFile)) {
            System.out.println("File renamed successfully");
        } else {
            System.out.println("Failed to rename file");
        }
        return filename_less_estension+"."+ext_downcase;
    }

    //*********************************************************************************************
    //ContaThread
    //*********************************************************************************************
    public int ContaThread() {
        ThreadGroup currentGroup1 = Thread.currentThread().getThreadGroup();
        int noThreads1 = currentGroup1.activeCount();
        Thread[] lstThreads1 = new Thread[noThreads1];
        currentGroup1.enumerate(lstThreads1);
        return lstThreads1.length;
    }
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    static byte[] getByte(String path) {
        byte[] getBytes = {};
        try {
            File file = new File(path);
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBytes;
    }
    //*********************************************************************************************
    //se apk Crash......
    //*********************************************************************************************
    public static class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler defaultUEH;

        private String localPath;

        private String url;

        /*
         * if any of the parameters is null, the respective functionality
         * will not be used
         */
        public CustomExceptionHandler(String localPath, String url) {
            this.localPath = localPath;
            this.url = url;
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        public void uncaughtException(Thread t, Throwable e) {
            //String timestamp = TimestampFormatter.getInstance().getTimestamp();
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            String stacktrace = result.toString();
            printWriter.close();

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            String timestamp = dateFormat.format(date);
            String timestamp1 = timestamp.replace(":","-");

            String filename = timestamp1 + ".deb";

            if (localPath != null) {
                writeToFile(stacktrace, filename);
            }
            //if (url != null) {
            //    sendToServer(stacktrace, filename);
            //}

            defaultUEH.uncaughtException(t, e);
        }

        private void writeToFile(String stacktrace, String filename) {
            try {
                BufferedWriter bos = new BufferedWriter(new FileWriter(
                        localPath + "/" + filename));
                bos.write(stacktrace);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * Function for open the Emergency page and clear the activities list
     *
     * @param context
     */
    public static void ClearActivitiesTopToEmergencyPage(Context context) {
        Intent intent = new Intent(context, Emergency_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
