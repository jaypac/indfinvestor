package com.indfinvestor.app.index.nse.bulkload;

import com.indfinvestor.app.index.model.dto.IndexCsvData;
import com.indfinvestor.app.index.model.dto.IndexNavDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

@Slf4j
public class NseFileItemProcessor implements ItemProcessor<IndexNavDetails, IndexNavDetails> {

    @Override
    public IndexNavDetails process(IndexNavDetails item) {

        IndexNavDetails newMfNavDetails = new IndexNavDetails();
        newMfNavDetails.setIndexName(item.getIndexName());
        log.info("Processing records for index {}", item.getIndexName());
        var historicalData = item.getHistoricalNavData();
        historicalData.sort((Comparator.comparing(IndexCsvData::getDate)));

        Map<LocalDate, IndexCsvData> navHistory =
                historicalData.stream().collect(Collectors.toMap(IndexCsvData::getDate, Function.identity()));

        List<IndexCsvData> navRecords = populateMissingDates(navHistory, historicalData);
        newMfNavDetails.setHistoricalNavData(navRecords);
        return newMfNavDetails;
    }

    private List<IndexCsvData> populateMissingDates(Map<LocalDate, IndexCsvData> navHistory, List<IndexCsvData> value) {
        List<IndexCsvData> records = new ArrayList<>();

        // Get the first date
        var firstDate = value.getFirst().getDate();
        var lastDate = value.getLast().getDate();

        // Loop through the date range
        for (LocalDate date = firstDate; !date.isAfter(lastDate); date = date.plusDays(1)) {
            // Check if the date is present in the navHistory map
            if (navHistory.containsKey(date)) {
                // If present, add the record to the list
                records.add(navHistory.get(date));
            } else {
                // If not present, search for the previous date
                var navRecord = new IndexCsvData();
                var count = 1;
                while (count <= 7) {
                    var oldDate = date.minusDays(count);
                    if (navHistory.containsKey(oldDate)) {
                        var oldRecord = navHistory.get(oldDate);
                        BeanUtils.copyProperties(oldRecord, navRecord);
                        navRecord.setDate(date);
                        records.add(navRecord);
                        break;
                    }

                    count++;
                }
            }
        }

        return records;
    }
}
