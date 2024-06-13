package com.indfinvestor.app.indexdata.entity;

import com.indfinvestor.app.indexdata.Exchange;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "IndexInfo")
@Table(name = "INDEX_INFO")
public class IndexInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "EXCHANGE")
    private Exchange exchange;

    @Column(name = "INDEX_NAME")
    private String indexName;

    @OneToMany(
            mappedBy = "indexInfo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<IndexSourceMapping> indexSourceMappings = new ArrayList<>();

    public void addCIndexMapping(IndexSourceMapping indexSourceMapping) {
        indexSourceMappings.add(indexSourceMapping);
        indexSourceMapping.setIndexInfo(this);
    }

    public void removeIndexMapping(IndexSourceMapping indexSourceMapping) {
        indexSourceMappings.remove(indexSourceMapping);
        indexSourceMapping.setIndexInfo(null);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
