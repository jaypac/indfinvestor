package com.indfinvestor.app.index.service;

import com.indfinvestor.app.index.model.entity.IndexDetails;
import com.indfinvestor.app.index.repository.IndexDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndexDetailsService {

    private final IndexDetailsRepository indexDetailsRepository;

    public IndexDetails getIndexByName(String name) {
        return indexDetailsRepository.findByName(name);
    }
}
