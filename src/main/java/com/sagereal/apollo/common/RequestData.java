package com.sagereal.apollo.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class RequestData {
    @JsonAlias(value = {"Customer Apollo"})
    String CustomerApollo;
    String ShipToCustomerID;
    String ShipToCustomerName;
    String SoldToCustomerID;
    String SoldToCustomerName;
    String Country;
    String Region;
    String ProductVariant;
    String ApprovedSWTag;
    String ApprovedSWID;
    String SWChangeType;
    String MAFeedback;
    String SIMLock;
    String ApprovalDate;
    String SKU;
    String ResponsibleTAM;
    String CreatedDate;
    String UpdatedDate;
    String IsDeleted;
    String DeletedDate;
    @JsonAlias(value = {"SW SKU ID"})
    String SW_SKU_ID;
    String SIMLockFileName;
}
