package com.sagereal.provider.pojo;

import com.sagereal.provider.common.ReplicationConverter;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
public class Replication {
    @Id
    @GeneratedValue
    Integer id;
    Date date;
    String cid;
    String note;
    String commiter;
    String email;
    Boolean finish;
    @Convert(converter = ReplicationConverter.class)
    @Column(length = 1024)
    List<String> list;
    String product;
}
