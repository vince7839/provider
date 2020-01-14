package com.sagereal.provider.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "phone_info")
@Data
public class PhoneInfo {
    @Id
    @GeneratedValue
    Integer id;
    String imei;
    Double longitude;
    Double latitude;
    Double temperature;
    Integer shake;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date date;
}
