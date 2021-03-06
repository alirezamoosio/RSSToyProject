package ir.sahab.rsstoyproject;

import asg.cliche.ShellFactory;
import ir.sahab.rsstoyproject.console.RequestHandler;
import ir.sahab.rsstoyproject.scraper.ScraperPool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * RSS feed reader!
 */
public class App {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        ScraperPool scraperPool = new ScraperPool("RSSDatabase");
        executor.scheduleAtFixedRate(scraperPool, 0, 1, TimeUnit.SECONDS);
        try {
            ShellFactory.createConsoleShell("RSSFeedReader", "", new RequestHandler())
                    .commandLoop();
        } catch (IOException e) {
            //TODO
            System.out.println(e.getMessage());
        }
    }
}
