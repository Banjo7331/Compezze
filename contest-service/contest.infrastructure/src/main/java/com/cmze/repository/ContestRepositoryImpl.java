package com.cmze.repository;

import com.cmze.entity.Contest;
import com.cmze.external.jpa.ContestJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class ContestRepositoryImpl implements ContestRepository{

    private final ContestJpaRepository impl;

    @Autowired
    public ContestRepositoryImpl(ContestJpaRepository impl) {
        this.impl = impl;
    }

    @Override
    public Contest findById(Long id) {
        return impl.findById(id).orElseThrow(()-> new RuntimeException("Contest not found"));
    }

    @Override
    public Page<Contest> findAll(Pageable pageable, Long quizId) {
        return impl.findAll(pageable);
    }

    @Override
    public Contest save(Contest contest) {
        return null;
    }

}
