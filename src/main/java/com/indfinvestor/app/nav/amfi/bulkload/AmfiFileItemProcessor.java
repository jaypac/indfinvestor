package com.indfinvestor.app.nav.amfi.bulkload;

import com.indfinvestor.app.nav.model.dto.MfNavDetails;
import com.indfinvestor.app.nav.model.dto.MfNavRecord;
import com.indfinvestor.app.nav.model.dto.MfSchemeDetailsRecord;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.BeanUtils;

@Slf4j
public class AmfiFileItemProcessor implements ItemProcessor<MfNavDetails, MfNavDetails> {

    @Override
    public MfNavDetails process(MfNavDetails item) {

        MfNavDetails newMfNavDetails = new MfNavDetails();
        newMfNavDetails.setFundHouse(item.getFundHouse());
        log.info("Processing records for fund {}", item.getFundHouse());
        var historicalData = item.getHistoricalNavData();
        historicalData.forEach((k, v) -> v.sort(Comparator.comparing(MfNavRecord::getDate)));

        Map<MfSchemeDetailsRecord, List<MfNavRecord>> historicalNavData = new HashMap<>();
        item.getHistoricalNavData().forEach((key, value) -> {
            Map<LocalDate, MfNavRecord> navHistory =
                    value.stream().collect(Collectors.toMap(MfNavRecord::getDate, Function.identity()));
            List<LocalDate> navHistoricalDates =
                    value.stream().map(MfNavRecord::getDate).collect(Collectors.toList());

            var isApplicable = filter(navHistoricalDates);
            if (isApplicable) {
                List<MfNavRecord> navRecords = populateMissingDates(navHistory, value);
                historicalNavData.put(key, navRecords);
            } else {
                log.info("Skipping records for scheme {} ", key.schemeName());
            }
        });

        newMfNavDetails.setHistoricalNavData(historicalNavData);
        return newMfNavDetails;
    }

    private boolean filter(List<LocalDate> navHistory) {

        var currentYear = LocalDate.now().getYear();
        var lastYear = navHistory.getLast().getYear();
        return currentYear - lastYear <= 1;
    }

    private List<MfNavRecord> populateMissingDates(Map<LocalDate, MfNavRecord> navHistory, List<MfNavRecord> value) {
        List<MfNavRecord> records = new ArrayList<>();

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
                var navRecord = new MfNavRecord();
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
