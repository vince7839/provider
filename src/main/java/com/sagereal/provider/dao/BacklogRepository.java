package com.sagereal.provider.dao;

import com.sagereal.provider.pojo.Backlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BacklogRepository extends JpaRepository<Backlog,Integer> {
    Page<Backlog> findAllByOrderByCreateTimeDesc(Pageable pageable);
    List<Backlog> findByState(String state);
}
