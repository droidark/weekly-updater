package xyz.krakenkat.weeklyupdater.writer;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import xyz.krakenkat.reader.dto.ItemDTO;
import xyz.krakenkat.weeklyupdater.model.Issue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
public class IssueWriter implements ItemWriter<List<ItemDTO>> {

    @Autowired
    private MongoOperations mongoOperations;

    private static final String COLLECTION_NAME = "issue";
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void write(List<? extends List<ItemDTO>> list) throws Exception {
        log.info("I'm in writer");
        log.info("List size: " + list.size());
        for(List<ItemDTO> itemDTOList : list) {
            log.info("ItemDTO size: " + itemDTOList.size());
            for (ItemDTO itemDTO : itemDTOList) {
//                log.info("Original Date: " + itemDTO.getDate());
//                log.info("Release Date: " + FORMAT.parse(itemDTO.getDate()));
                Issue dbIssue = mongoOperations.findOne(
                        Query.query(Criteria
                                .where("title")
                                .is(new ObjectId(itemDTO.getTitleId()))
                                .and("key")
                                .is(itemDTO.getNumber().toString())
                                .and("variant")
                                .is(false)),
                        Issue.class, COLLECTION_NAME);

                if (dbIssue == null) {
                    mongoOperations.save(Issue
                            .builder()
                            .title(new ObjectId(itemDTO.getTitleId()))
                            .name(itemDTO.getName())
                            .key(Integer.toString(itemDTO.getNumber()))
                            .number(Double.valueOf(itemDTO.getNumber()))
                            .cover(itemDTO.getCover())
                            .pages(itemDTO.getPages())
                            .printedPrice(itemDTO.getPrice())
                            .currency(itemDTO.getCurrency())
                            .releaseDate(FORMAT.parse(itemDTO.getDate()))
                            .shortReview(itemDTO.getShortDescription())
                            .isbn(itemDTO.getIsbn())
                            .edition(itemDTO.getEdition())
                            .variant(false)
                            .build());
                } else {
                    log.info("The issue " + dbIssue.getName() + " already exists in the DB");
                }
            }
        }
    }
}
