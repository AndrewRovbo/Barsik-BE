package com.barsik.backend.api.DTO.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OwnerProfileUpdateRequest {
    private String aboutMe;
    private Boolean ownerVerified;
}
