package com.example.unimap.service.minimalPath;

import java.util.*;

public class Djikstra {
    private ArrayList<Edge> edges;
    // maps convert real room numbers into our index integers and vise versa
    private HashMap<String, Integer> realRoomToOur = new HashMap<>();
    private HashMap<Integer, String> ourRoomToReal = new HashMap<>();
    private ArrayList<Integer>[] adjacent;        // vertex n has adjacent[n] neighbours
    private Integer[] parent;                     // vertex n's parent vertex is parent[n]
    private double[] dist;                        // distance from source vertex to n is dist[n]
    private double[][] edgeWeight;                // weight of a-b edge is edgeWeight[a][b]
    private Queue<Integer> exploredVertexes;      // explored but yet unprocessed vertexes

    Djikstra(ArrayList<Edge> edges, int graphSize) {
        this.edges = edges;
        adjacent = new ArrayList[graphSize];
        parent = new Integer[graphSize];
        dist = new double[graphSize];
        edgeWeight = new double[graphSize][graphSize];        // vertexes are sorted due to distance
        exploredVertexes = new PriorityQueue<>((a, b) -> (int) (dist[a] - dist[b]));  // from source

        mapCreator();
        fillAdjacentAndEdgeWeight();
    }

    private void mapCreator() {
        String startV, endV;
        int index = 0;

        for (Edge edge : edges) {
            startV = edge.getStartVertex();
            endV = edge.getEndVertex();

            if (!realRoomToOur.keySet().contains(startV)) {
                realRoomToOur.put(startV, index);
                ourRoomToReal.put(index++, startV);
            }
            if (!realRoomToOur.keySet().contains(endV)) {
                realRoomToOur.put(endV, index);
                ourRoomToReal.put(index++, endV);
            }
        }
    }


    private void fillAdjacentAndEdgeWeight() {
        int startV, endV;                           // starting vertex and ending vertex of edge
        for (Edge edge : edges) {
            startV = realRoomToOur.get(edge.getStartVertex());
            endV = realRoomToOur.get(edge.getEndVertex());

            addAdj(startV, endV);
            edgeWeight[startV][endV] = edge.getEdgeWeight();
        }
    }


    private void addAdj(int startV, int endV) {
        if (adjacent[startV] == null) {
            adjacent[startV] = new ArrayList<>();
        }
        adjacent[startV].add(endV);
    }


    private void zerothIteration() {
        Arrays.fill(parent, null);
        Arrays.fill(dist, Integer.MAX_VALUE);
    }


    public String minPathBetweenPoints(String source, String finish) {
        zerothIteration();
        Integer ourSource = realRoomToOur.get(source);
        Integer ourFinish = realRoomToOur.get(finish);

        if(ourSource == null || ourFinish == null){         // means eather room is invalid
            return null;
        }
        int processingV;

        dist[ourSource] = 0;
        exploredVertexes.add(ourSource);

        while ((processingV = exploredVertexes.remove()) != ourFinish) {
            for (int adjV : adjacent[processingV]) {
                if (dist[adjV] > dist[processingV] + edgeWeight[processingV][adjV]) {
                    dist[adjV] = dist[processingV] + edgeWeight[processingV][adjV];
                    parent[adjV] = processingV;
                    exploredVertexes.add(adjV);
                }
            }
        }

        int pathLength = (int) Math.round(dist[ourFinish]);    // as last element of our min path

        return constractPath(ourFinish) + " -> " + pathLength;
    }


    private String constractPath(int ourFinish) {
        ArrayList<String> path = new ArrayList<>();
        Integer oneParent = ourFinish;

        while (parent[oneParent] != null) {
            path.add(ourRoomToReal.get(oneParent));
            oneParent = parent[oneParent];
        }
        path.add(ourRoomToReal.get(oneParent));

        Collections.reverse(path);

        return String.join(" -> ", path);
    }
}
