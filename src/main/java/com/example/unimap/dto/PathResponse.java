package com.example.unimap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PathResponse {
    private String path;
    private int cost;
}
