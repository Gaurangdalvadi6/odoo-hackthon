package com.fleetflow.dto.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MeResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private List<String> permissions;
}
