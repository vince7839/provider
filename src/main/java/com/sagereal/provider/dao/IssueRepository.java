package com.sagereal.provider.dao;

import com.sagereal.provider.pojo.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Integer>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByStateAndModifyDateBefore(String state, Date date);
}
