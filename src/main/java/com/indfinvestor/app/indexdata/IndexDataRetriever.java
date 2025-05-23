package com.indfinvestor.app.indexdata;

import com.indfinvestor.app.indexdata.sink.IndexDataSink;
import com.indfinvestor.app.indexdata.sink.SinkType;
import com.indfinvestor.app.indexdata.source.IndexDataSource;
import com.indfinvestor.app.indexdata.source.IndexSourceType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndexDataRetriever {

    private final List<IndexDataSource> indexDataSources;
    private final List<IndexDataSink> indexDataSinks;

    public IndexDataRetriever(List<IndexDataSource> indexDataSources, List<IndexDataSink> indexDataSinks) {
        this.indexDataSources = indexDataSources;
        this.indexDataSinks = indexDataSinks;
    }

    public void process(IndexDataFetchParams indexDataFetchParams) {

        IndexDataSource indexDataSource = getIndexDataSource(indexDataFetchParams.indexSourceType());
        IndexDataSink indexDataSink = getIndexDataSink(indexDataFetchParams.sinkType());
        indexDataSource.fetch(indexDataFetchParams, indexDataSink);

    }

    private IndexDataSink getIndexDataSink(SinkType sinkType) {
        String errorMsg = "No sink found for %s";
        return indexDataSinks.stream()
                .filter(it -> it.getType().equals(sinkType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(errorMsg, sinkType.name())));
    }

    private IndexDataSource getIndexDataSource(IndexSourceType indexSourceType) {
        String errorMsg = "No datasource found for %s";
        return indexDataSources.stream()
                .filter(it -> it.getType().name().equals(indexSourceType.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(errorMsg, indexSourceType.name())));
    }
}
