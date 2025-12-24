package com.example.unimap.service.minimalPath;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class Edge {
    private final String startVertex;
    private final String endVertex;
    private final double edgeWeight;
}
