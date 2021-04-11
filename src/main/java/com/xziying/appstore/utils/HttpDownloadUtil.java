package com.xziying.appstore.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * HttpDownloadUtil
 *
 * @author : xziying
 * @create : 2021-04-04 11:40
 */
public class HttpDownloadUtil {

    public static boolean download(String urlStr, String file){
        return download(urlStr, new File(file));
    }

    public static boolean download(String urlStr, File file){
        int byteread = 0;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(file);

            byte[] buffer = new byte[1024 * 4];
            while ((byteread = inStream.read(buffer)) != -1) {
                fs.write(buffer, 0, byteread);
            }
            fs.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
