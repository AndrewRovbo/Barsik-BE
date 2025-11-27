package com.barsik.backend.api.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Setter
@Getter
public class PetRequest {
    private String name;
    private String type;
    private String breed;
    private Byte age;
    private String gender;
    private String description;
    private String photoUrl;
}
