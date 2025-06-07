package com.indfinvestor.app.index.model.entity.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexNavDetails implements Serializable {
    @NotBlank private String indexName;

    private List<IndexCsvData> historicalNavData;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        IndexNavDetails that = (IndexNavDetails) o;
        return indexName.equals(that.indexName);
    }

    @Override
    public int hashCode() {
        return indexName.hashCode();
    }
}
