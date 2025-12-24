package com.example.unimap.controller;

import com.example.unimap.dto.*;
import com.example.unimap.service.minimalPath.MinimalPathService;
import com.example.unimap.service.schedule.ScheduleService;
import com.example.unimap.service.staffRoom.StaffRoomService;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/")
public class GaniviController {
    private final MinimalPathService minPathService;
    private final StaffRoomService staffService;

    private GaniviController(MinimalPathService minPathService, StaffRoomService staffService) {
        this.minPathService = minPathService;
        this.staffService = staffService;
    }

    // Endpoint to get the shortest path
    @PostMapping("/minimal-path")
    public ResponseEntity<PathResponse> getShortestPath(@RequestBody PathRequest pathReq) {
        String minPath = minPathService.findShortestPath(pathReq.getStart(), pathReq.getEnd());
        PathResponse response = minPathService.makePathResponse(minPath);
        return ResponseEntity.ok(response);
    }

    // Endpoint to get the staff room number by full name
    @GetMapping("/staff-room")
    public ResponseEntity<String> getStaffRoom(@RequestParam String staffFullName) {
        String room = staffService.findStaffRoom(staffFullName);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> getSchedule(@RequestBody ScheduleRequest request) {
        String newSchedule = ScheduleService.getSchedule(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(newSchedule);
    }
}