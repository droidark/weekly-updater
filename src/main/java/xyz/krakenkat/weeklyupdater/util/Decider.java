package xyz.krakenkat.weeklyupdater.util;

import xyz.krakenkat.reader.lector.*;
import xyz.krakenkat.weeklyupdater.dto.TitleDTO;

public class Decider {

    private Decider() {}

    public static Lector getInstance(String path, String publisher, TitleDTO titleDTO) {
        return switch (publisher) {
            case "panini-manga-mx" -> PaniniLectorV2
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getPublisherUrl())
                    .path(path)
                    .folder(publisher)
                    .download(Boolean.FALSE)
                    .build();
            case "kamite-manga" -> KamiteLectorV2
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getPublisherUrl())
                    .path(path)
                    .folder(publisher)
                    .download(Boolean.FALSE)
                    .build();
            case "distrito-manga-mx" -> DistritoMangaLector
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getPublisherUrl())
                    .path(path)
                    .folder(publisher)
                    .download(Boolean.FALSE)
                    .build();
            case "mangaline-mx" -> MangaLineLector
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getPublisherUrl())
                    .path(path)
                    .folder(publisher)
                    .download(Boolean.FALSE)
                    .build();
            case "viz" -> VizLector
                    .builder()
                    .titleId(titleDTO.getTitle())
                    .key(titleDTO.getKey())
                    .url(titleDTO.getPublisherUrl())
                    .path(path)
                    .folder(publisher)
                    .download(Boolean.FALSE)
                    .build();
            default -> null;
        };
    }
}
