package com.core.gsbaseandroid.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author jon
 */
public class Decompress {
    private String zipFile;
    private String location;

    public Decompress(String zipFile, String location) {
        this.zipFile = zipFile;
        this.location = location;
        dirChecker("");
    }

    public void unzip(Callback callback) {
        try {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                String zeName;
                if (ze.getName().contains("/")) {
                    String[] name = ze.getName().split("/");
                    zeName = name[name.length - 1];
                } else {
                    zeName = ze.getName();
                }
                File file = new File(location + "/" + zeName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = zin.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, read);
                }
                bufferedOutputStream.close();
                zin.closeEntry();
                fileOutputStream.close();
                if (file.length() == 0) {
                    file.delete();
                }
            }
            zin.close();
            if (callback != null) {
                callback.callbackSuccess();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callback != null) {
                callback.callbackFailed();
            }
        }
    }

    private void dirChecker(String dir) {
        File f = new File(location + dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public interface Callback {
        void callbackSuccess();

        void callbackFailed();
    }
} 