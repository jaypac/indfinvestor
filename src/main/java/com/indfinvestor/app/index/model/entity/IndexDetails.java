package com.indfinvestor.app.index.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

@Getter
@Setter
@Entity
@Table(name = "INDEX_DETAILS")
public class IndexDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "INDEX_TYPE", nullable = false)
    private String indexType;

    @Column(name = "SUB_INDEX_TYPE", nullable = false)
    private String subIndexType;

    @Column(name = "EXCHANGE", nullable = false)
    private String exchange;
}
