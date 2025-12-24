package com.example.unimap.service.minimalPath;

import com.example.unimap.dto.PathResponse;
import com.example.unimap.entity.MinimalPath;
import com.example.unimap.exception.InvalidInputException;
import com.example.unimap.exception.ResourceNotFoundException;
import com.example.unimap.repository.MinimalPathRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Service
public class MinimalPathService {
    private MinimalPathRepo pathRepo;

    private MinimalPathService(MinimalPathRepo pathRepo) {
        this.pathRepo = pathRepo;
    }

    private String getMinimalPath(String points) {          // from Optional to String
        Optional<MinimalPath> minPath = pathRepo.findById(points);
        return minPath.isPresent() ? minPath.get().getPathBetween() : null;
    }


    public String findShortestPath(String start, String end) {
        if (end == null || end.trim().isEmpty())
            throw new InvalidInputException("End room cannot be empty.");

        String minimalPath;

        minimalPath = getMinimalPath(start + " -> " + end);
        if (minimalPath != null) {
            return minimalPath;
        }

        minimalPath = getMinimalPath(end + " -> " + start);
        if (minimalPath != null) {
            return pathReverser(minimalPath);
        }

        minimalPath = Graph.minPathBetweenPoints(start, end);
        if (minimalPath != null) {                              // if rooms were valid
            pathRepo.save(new MinimalPath(start + " -> " + end, minimalPath));
        } else {
            throw new ResourceNotFoundException("One of the rooms doesn't exist.");
        }

        return minimalPath;
    }

    private String pathReverser(String path) {
        String[] points = path.split(" -> ");
        ArrayList<String> listPoints = new ArrayList<>(Arrays.asList(points));
        String lastElement = listPoints.remove(listPoints.size() - 1);
        Collections.reverse(listPoints);
        listPoints.add(lastElement);
        return String.join(" -> ", listPoints);
    }


    public PathResponse makePathResponse(String minPath) {
        int lastArrowIndex = minPath.lastIndexOf(" -> ");
        String strCost = minPath.substring(lastArrowIndex + 4);
        int cost = Integer.parseInt(strCost);
        String minPathWithoutCost = minPath.substring(0, lastArrowIndex);

        return new PathResponse(minPathWithoutCost, cost);
    }
}
