package xyz.krakenkat.weeklyupdater.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TitleDTO {
    private String key;
    private String title;
    private String whakoomUrl;
    private String publisherUrl;
}
