package com.G2T5203.wingit.seat;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SeatController {
    private final SeatService service;

    public SeatController(SeatService service) { this.service = service; }

    @GetMapping(path = "/seats/{planeId}")
    public List<SeatSimpleJson> getAllSeatsForPlane(@PathVariable String planeId) {
        try {
            return service.getAllSeatsForPlaneAsSimpleJson(planeId);
        } catch (Exception e) {
            throw new SeatBadRequestException(e);
        }
    }
}
