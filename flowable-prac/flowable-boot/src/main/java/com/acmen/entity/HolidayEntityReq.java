package com.acmen.entity;

import lombok.Data;
import lombok.NonNull;

@Data
public class HolidayEntityReq {

    @NonNull
    private String userId;

    private Integer holidays;

    private String description;

    @NonNull
    private String approverId;

}
