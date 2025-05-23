package com.indfinvestor.app.indexdata.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "INDEX_SOURCE_MAPPING")
public class IndexSourceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private IndexInfo indexInfo;

    @Column(name = "code")
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IndexInfo getIndexInfo() {
        return indexInfo;
    }

    public void setIndexInfo(IndexInfo indexInfo) {
        this.indexInfo = indexInfo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexSourceMapping )) return false;
        return id != null && id.equals(((IndexSourceMapping) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
