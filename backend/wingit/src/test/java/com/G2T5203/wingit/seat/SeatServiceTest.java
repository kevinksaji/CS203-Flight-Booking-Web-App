package com.G2T5203.wingit.seat;

import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneNotFoundException;
import com.G2T5203.wingit.plane.PlaneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PlaneRepository planeRepository;

    @Test
    void getAllSeats_multipleSeats_Success() {
        // arrange
        final String dummyPlaneId = "DUMMY_PLANE_ID";
        List<Seat> expectedSeats = new ArrayList<>();
        when(seatRepository.findAllBySeatPkPlanePlaneId(dummyPlaneId)).thenReturn(expectedSeats);
        when(planeRepository.existsById(dummyPlaneId)).thenReturn(true);

        // act
        List<SeatSimpleJson> result = seatService.getAllSeatsForPlaneAsSimpleJson("DUMMY_PLANE_ID");

        // verify
        verify(seatRepository).findAllBySeatPkPlanePlaneId(dummyPlaneId);
        verify(planeRepository).existsById(dummyPlaneId);
        assertEquals(expectedSeats.size(), result.size());
    }

    @Test
    void createSeat_Success() {
        // Arrange
        SeatSimpleJson seatSimpleJson = new SeatSimpleJson("Plane123", "A1", "Economy", 1.0);
        Plane plane = new Plane("Plane123", 5, "Plane Model");
        when(planeRepository.findById("Plane123")).thenReturn(Optional.of(plane));

        // create a new Seat with valid seatClass
        Seat createdSeat = new Seat();
        createdSeat.setSeatClass("Economy"); // Set the seatClass
        createdSeat.setPriceFactor(1.0);
        // mock the save operation to return the createdSeat
        when(seatRepository.save(any(Seat.class))).thenReturn(createdSeat);

        // act
        createdSeat = seatService.createSeat(seatSimpleJson);

        // verify
        assertNotNull(createdSeat);
        assertEquals("Economy", createdSeat.getSeatClass()); // Check the seatClass
        assertEquals(1.0, createdSeat.getPriceFactor()); // Check the priceFactor
        verify(seatRepository).save(any(Seat.class));
    }

    @Test
    void createSeat_PlaneNotFound_Failure() {
        // arrange
        SeatSimpleJson seatSimpleJson = new SeatSimpleJson("NonExistentPlane", "A1", "Economy", 1.0);
        when(planeRepository.findById("NonExistentPlane")).thenReturn(Optional.empty());

        // act and verify
        PlaneNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(
                PlaneNotFoundException.class,
                () -> seatService.createSeat(seatSimpleJson)
        );

        assertEquals("Could not find plane NonExistentPlane", exception.getMessage());
        verify(planeRepository).findById("NonExistentPlane");
    }

    @Test
    void createSeat_SeatAlreadyExists_Failure() {
        // arrange
        SeatSimpleJson seatSimpleJson = new SeatSimpleJson("Plane123", "A1", "Economy", 1.0);
        Plane plane = new Plane("Plane123", 5, "Plane Model");
        when(planeRepository.findById("Plane123")).thenReturn(Optional.of(plane));
        SeatPk seatPk = new SeatPk(plane, "A1");
        when(seatRepository.existsById(seatPk)).thenReturn(true);

        // act and verify
        SeatBadRequestException exception = org.junit.jupiter.api.Assertions.assertThrows(
                SeatBadRequestException.class,
                () -> seatService.createSeat(seatSimpleJson)
        );

        assertEquals("BAD REQUEST: Seat already exists.", exception.getMessage());

        verify(planeRepository).findById("Plane123");
        verify(seatRepository).existsById(seatPk);
    }
}

