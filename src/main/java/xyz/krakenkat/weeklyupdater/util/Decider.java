package xyz.krakenkat.weeklyupdater.util;

import xyz.krakenkat.reader.lector.KamiteLector;
import xyz.krakenkat.reader.lector.Lector;
import xyz.krakenkat.reader.lector.PaniniLectorV2;
import xyz.krakenkat.weeklyupdater.dto.TitleDTO;

public class Decider {
    public static Lector getInstance(String path, String publisher, TitleDTO titleDTO) {
        if (publisher.equals("panini-manga-mx")) {
            return PaniniLectorV2
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getPublisherUrl())
                    .path(path)
                    .folder(publisher)
                    .download(false)
                    .build();
        } else if (publisher.equals("kamite-manga")) {
            return KamiteLector
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getWhakoomUrl())
                    .path(path)
                    .folder(publisher)
                    .download(true)
                    .build();
        }
        return null;
    }
}
