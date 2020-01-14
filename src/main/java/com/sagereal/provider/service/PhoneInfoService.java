package com.sagereal.provider.service;

import com.sagereal.provider.dao.PhoneInfoRepository;
import com.sagereal.provider.pojo.PhoneInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PhoneInfoService {

    @Autowired
    PhoneInfoRepository repositoty;

    public PhoneInfo savePhoneInfo(PhoneInfo info) {
        return repositoty.save(info);
    }

    public List<PhoneInfo> findByImei(String imei) {
        return repositoty.findByImei(imei);
    }

    @Transactional
    public void deleteAllByImei(String imei) {
        repositoty.deleteAllByImei(imei);
    }

    public List<PhoneInfo> findByPage(Pageable pageable) {
        Page<PhoneInfo> page = repositoty.findAllByOrderByDateDesc(pageable);
        List<PhoneInfo> list = page.getContent();
        return list;
    }

    public long getCount() {
        return repositoty.count();
    }
}
