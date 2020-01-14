package com.sagereal.provider.dao;

import com.sagereal.provider.pojo.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Integer> {
    List<Issue> findAllBySubmitDateAfterOrderByModifyDateAsc(Date date);
    List<Issue> findAllByOrderByModifyDateAsc();
    Page<Issue> findAllByProposerContainsOrderByModifyDateDesc(String proposer, Pageable pageable);
    Page<Issue> findByOrderByModifyDateDesc(Pageable pageable);
    List<Issue> findByStateAndModifyDateBefore(String state,Date date);
}
