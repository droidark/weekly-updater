package xyz.krakenkat.weeklyupdater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeeklyUpdaterApplication {

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(WeeklyUpdaterApplication.class, args)));
    }
}
