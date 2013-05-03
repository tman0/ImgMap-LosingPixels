package com.tenko.utils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.tenko.ImgMap;

/**
 * URLUtils used in ImgMap for content-type checking.
 * @author Tsunko
 */
public class URLUtils {

	/**
	 * 
	 * @return
	 */
	public static String getLocal(String localName) throws SecurityException, IOException {
		String tmp = "";

		for(File f : new File(ImgMap.getPlugin().getDataFolder(), "images").listFiles()){
			if(f.getName().equalsIgnoreCase(localName)){
				tmp = f.getCanonicalPath();
				break;
			}
		}

		return tmp;
	}

	public static boolean isLocal(String fileName){
		for(File f : new File(ImgMap.getPlugin().getDataFolder(), "images").listFiles()){
			if(f.getName().equalsIgnoreCase(fileName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if the image is a proper type. Currently uses Content-types.
	 * @param s - The URL to try to check.
	 * @return True if the image is compatible; false otherwise.
	 */
	public static boolean compatibleImage(String s){
		try {
			String contentType = getContentType(new URL(s));
			if(contentType.startsWith("image")){
				return true;
			}
		} catch (MalformedURLException e){
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Gets the content type. Do not use outside of this class. No point.
	 * @param theURL - The URL object.
	 * @return A string featuring the content-type.
	 * @throws IOException
	 */
	private final static String getContentType(URL theURL) throws IOException {
		//Attempt to reconsutrct HTTPS URLs. Most likely to fail.
		if(theURL.getProtocol().equalsIgnoreCase("https")){
			theURL = new URL(fixEncryptedUrl(theURL.toExternalForm()));		
			System.out.println("http://" + theURL.getHost() + theURL.getFile());
		}

		HttpURLConnection con = (HttpURLConnection)theURL.openConnection();
		con.setRequestMethod("HEAD");
		con.connect();
		String toReturn = con.getContentType();
		con.disconnect();

		return toReturn;
	}

	public static String fixEncryptedUrl(String s){
		return s.replaceFirst("https", "http");
	}

}