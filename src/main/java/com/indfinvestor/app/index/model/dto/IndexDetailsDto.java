package com.indfinvestor.app.index.model.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexDetailsDto implements Serializable {

    private Long id;
    private String name;
    private String indexType;
    private String subIndexType;
    private String exchange;

    public IndexDetailsDto(Long id, String name, String indexType, String subIndexType, String exchange) {
        this.id = id;
        this.name = name;
        this.indexType = indexType;
        this.subIndexType = subIndexType;
        this.exchange = exchange;
    }

    private List<IndexNavDto> indexNavs;
}
