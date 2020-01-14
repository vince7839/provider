package com.sagereal.provider.dao;

import com.sagereal.provider.pojo.PhoneInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface PhoneInfoRepository extends JpaRepository<PhoneInfo,Integer> {
    List<PhoneInfo> findByImei(String imei);
    List<PhoneInfo> findByDate(Date date);
    Page<PhoneInfo> findAllByOrderByDateDesc(Pageable pageable);
    @Modifying
    @Query("delete from phone_info where imei = ?1")
    void deleteAllByImei(String imei);
}
