package com.sagereal.provider.dao;

import com.sagereal.provider.pojo.Replication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplicationRepository extends JpaRepository<Replication,Integer> {

}
