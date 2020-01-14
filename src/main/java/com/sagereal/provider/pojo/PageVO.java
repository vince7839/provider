package com.sagereal.provider.pojo;

import lombok.Data;
import java.util.List;

@Data
public class PageVO {
    Long count;
    Integer page;
    List data;
}
