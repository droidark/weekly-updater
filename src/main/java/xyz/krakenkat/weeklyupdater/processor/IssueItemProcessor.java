package xyz.krakenkat.weeklyupdater.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import xyz.krakenkat.reader.dto.ItemDTO;
import xyz.krakenkat.reader.lector.WhakoomLector;
import xyz.krakenkat.reader.util.Utilities;
import xyz.krakenkat.weeklyupdater.dto.TitleDTO;
import xyz.krakenkat.weeklyupdater.model.Issue;
import xyz.krakenkat.weeklyupdater.util.Decider;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class IssueItemProcessor implements ItemProcessor<TitleDTO, List<ItemDTO>> {

    private final MongoOperations mongoOperations;

    @Value("${weekly-updater.images.path}")
    private String path;

    @Value("${weekly-updater.publisher}")
    private String folder;

    @Override
    public List<ItemDTO> process(final TitleDTO titleDTO) {
        log.info(String.format("PROCESSOR: Getting information for %s", titleDTO.getKey()));
        List<ItemDTO> databaseList = getDatabaseList(titleDTO.getTitle());
        List<ItemDTO> whakoomList = WhakoomLector
                .builder()
                .titleId(titleDTO.getTitle())
                .key(titleDTO.getKey())
                .url(titleDTO.getWhakoomUrl())
                .path(path)
                .folder(folder)
                .download(true)
                .build()
                .getDetails(databaseList);

        log.info(String.format("PROCESSOR: Items collected for %s: %d", titleDTO.getKey(), whakoomList.size()));

        return Utilities.joinLists(whakoomList, Objects.requireNonNull(Decider.getInstance(path, folder, titleDTO)).getDetails(databaseList));
    }

    private List<ItemDTO> getDatabaseList(String titleId) {
        return mongoOperations.find(
                Query.query(Criteria
                        .where("title")
                        .is(new ObjectId(titleId))),
                Issue.class, "issue")
                .stream()
                .map(issue -> ItemDTO
                        .builder()
                        .titleId(issue.getTitle().toString())
                        .number(issue.getNumber().intValue())
                        .variant(issue.getVariant())
                        .build())
                .toList();
    }
}
