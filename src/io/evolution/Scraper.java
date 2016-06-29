package io.evolution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Research on 6/29/2016.
 */
public class Scraper {


    private String link = "";

    public Scraper(String link) {
        this.link = link;
    }

    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper("http://tidesandcurrents.noaa.gov/stations.html");
        System.out.println(scraper.scrape(scraper.link));
    }

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

    boolean download(String name, String extension) {

        return true;
    }

    String getContent() {
        String content = "";
        return content;
    }
}
