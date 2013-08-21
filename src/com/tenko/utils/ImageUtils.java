package com.tenko.utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.tenko.ImgMap;
import sun.net.www.protocol.file.FileURLConnection;

public class ImageUtils {
	
	public static File getLocalImage(String imageName){
		File[] dir = new File(ImgMap.getInstance().getDataFolder(), "images").listFiles();
		
		for(File f : dir){
			if(f.getName().equalsIgnoreCase(imageName)){
				return f;
			}
		}
		
		return null;
	}
	
	public static boolean isLocal(String fileName){
		return getLocalImage(fileName) != null;
	}
	
	public static boolean isImageCompatible(String url){
		try {
			URL theURL = new URL(url);
			URLConnection con = theURL.openConnection();
            String toReturn = "false";
            if(con instanceof HttpURLConnection){
                HttpURLConnection http = (HttpURLConnection)con;
                http.setRequestMethod("HEAD");
                http.connect();
                toReturn = http.getContentType();
                http.disconnect();
            }
            else if(con instanceof FileURLConnection){
                FileURLConnection file = (FileURLConnection)con;
                file.connect();
                toReturn = file.getContentType();
                file.close();
            }

			return toReturn.startsWith("image");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}