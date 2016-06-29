package io.evolution;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by Research on 6/29/2016.
 */
public class Scraper {
    /**
     * main.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper();
        scraper.download("http://www.ndbc.noaa.gov/kml/marineobs_as_kml.php?sort=owner","testfile.kml");
    }

    /**
     * This method takes an address as an input-
     * and returns the content of the page as a string.
     *
     * @param str the str
     * @return the string
     * @throws IOException the io exception
     */
    public String scrape(String str) throws IOException {
        String content = "";
        // Make a URL to the web page
        URL url = new URL(str);

        // Get the input stream through URL Connection
        URLConnection con = url.openConnection();

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String line = null;

        // read each line and write to System.out
        while ((line = br.readLine()) != null) {
            content += line;
        }

        return content;

    }

    /**
     * This method takes in an address and a filename-
     * then downloads the page and saves that page as a file with the name specified.
     * @param address the address
     * @param fName   the f name
     * @return the boolean
     * @throws IOException the io exception
     */
    boolean download(String address, String fName) throws IOException {

        URL website = new URL(address);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(fName);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        return true;
    }

}
