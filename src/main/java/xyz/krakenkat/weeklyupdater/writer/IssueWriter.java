package xyz.krakenkat.weeklyupdater.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import xyz.krakenkat.reader.dto.ItemDTO;
import xyz.krakenkat.weeklyupdater.model.Issue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class IssueWriter implements ItemWriter<List<ItemDTO>> {

    private final MongoOperations mongoOperations;

    private static final String COLLECTION_NAME = "issue";

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void write(Chunk<? extends List<ItemDTO>> chunk) {
        log.info("Preparing issues to populate de DB");
        log.info("List size: {}", chunk.getItems().size());

        chunk.getItems()
                .stream()
                .flatMap(List::stream)
                .map(this::writeItem)
                .forEach(itemDTO -> log.info("{} was saved on the DB", itemDTO.getName()));
    }

    private ItemDTO writeItem(ItemDTO itemDTO) {
        if (!existsIssue(itemDTO)) {
            buildIssue(itemDTO).ifPresent(mongoOperations::save);
        }
        return itemDTO;
    }

    private boolean existsIssue(ItemDTO itemDTO) {
        return mongoOperations.exists(
                Query.query(Criteria
                        .where("title")
                        .is(new ObjectId(itemDTO.getTitleId()))
                        .and("key")
                        .is(itemDTO.getNumber().toString())
                        .and("variant")
                        .is(false)),
                Issue.class, COLLECTION_NAME);
    }

    private Optional<Issue> buildIssue(ItemDTO itemDTO) {
        try {
            return Optional.ofNullable(Issue
                    .builder()
                    .title(new ObjectId(itemDTO.getTitleId()))
                    .name(itemDTO.getName())
                    .key(Integer.toString(itemDTO.getNumber()))
                    .number(Double.valueOf(itemDTO.getNumber()))
                    .cover(itemDTO.getCover())
                    .pages(itemDTO.getPages())
                    .printedPrice(itemDTO.getPrice())
                    .currency(itemDTO.getCurrency())
                    .releaseDate(format.parse(itemDTO.getDate()))
                    .shortReview(itemDTO.getShortDescription())
                    .isbn(itemDTO.getIsbn())
                    .edition(itemDTO.getEdition())
                    .variant(false)
                    .build());
        } catch (ParseException e) {
            log.error("There was an error parsing date", e);
        }
        return Optional.empty();
    }
}
