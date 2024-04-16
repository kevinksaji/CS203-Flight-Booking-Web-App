package com.G2T5203.wingit.plane;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class PlaneServiceTest {

    @InjectMocks
    private PlaneService planeService;

    @Mock
    private PlaneRepository planeRepository;

    @Test
    void getAllPlanes_Success() {
        // arrange
        List<Plane> planes = new ArrayList<>();
        when(planeRepository.findAll()).thenReturn(planes);

        // act
        List<Plane> result = planeService.getAllPlanes();

        // verify
        assertEquals(planes, result);
        verify(planeRepository).findAll();
    }

    @Test
    void getById_PlaneExists_Success() {
        // arrange
        String planeId = "Plane123";
        Plane plane = new Plane(planeId, 5, "Plane Model");
        when(planeRepository.findById(planeId)).thenReturn(Optional.of(plane));

        // act
        Plane result = planeService.getById(planeId);

        // verify
        assertNotNull(result);
        assertEquals(planeId, result.getPlaneId());
        verify(planeRepository).findById(planeId);
    }

    @Test
    void getById_PlaneNotFound_Failure() {
        // arrange
        String planeId = "NonExistentPlane";
        when(planeRepository.findById(planeId)).thenReturn(Optional.empty());

        // act
        Plane result = planeService.getById(planeId);

        // verify
        assertNull(result);
        verify(planeRepository).findById(planeId);
    }

    @Test
    void createPlane_Success() {
        // arrange
        Plane newPlane = new Plane("NewPlane", 3, "New Model");
        when(planeRepository.existsById(newPlane.getPlaneId())).thenReturn(false);
        when(planeRepository.save(newPlane)).thenReturn(newPlane);

        // act
        Plane result = planeService.createPlane(newPlane);

        // verify
        assertNotNull(result);
        assertEquals(newPlane.getPlaneId(), result.getPlaneId());
        verify(planeRepository).existsById(newPlane.getPlaneId());
        verify(planeRepository).save(newPlane);
    }

    @Test
    void createPlane_PlaneIdExists_Failure() {
        // arrange
        Plane existingPlane = new Plane("ExistingPlane", 3, "Existing Model");
        when(planeRepository.existsById(existingPlane.getPlaneId())).thenReturn(true);

        // act and verify
        PlaneBadRequestException exception = assertThrows(PlaneBadRequestException.class, () -> {
            planeService.createPlane(existingPlane);
        });
        assertEquals("BAD REQUEST: PlaneId already exists.", exception.getMessage());
        verify(planeRepository).existsById(existingPlane.getPlaneId());
    }

    @Test
    void deletePlaneById_PlaneExists_Success() {
        // arrange
        String planeId = "Plane123";
        when(planeRepository.existsById(planeId)).thenReturn(true);

        // act and verify
        assertDoesNotThrow(() -> planeService.deletePlaneById(planeId));
        verify(planeRepository).deleteById(planeId);
        verify(planeRepository).existsById(planeId);
    }

    @Test
    void deletePlaneById_PlaneNotFound_Failure() {
        // arrange
        String planeId = "NonExistentPlane";
        when(planeRepository.existsById(planeId)).thenReturn(false);

        // act and verify
        PlaneNotFoundException exception = assertThrows(PlaneNotFoundException.class, () -> {
            planeService.deletePlaneById(planeId);
        });
        assertEquals("Could not find plane " + planeId, exception.getMessage());
        verify(planeRepository).existsById(planeId);
    }

    @Test
    void updatePlane_PlaneExists_Success() {
        // arrange
        Plane updatedPlane = new Plane("UpdatedPlane", 4, "Updated Model");
        when(planeRepository.existsById(updatedPlane.getPlaneId())).thenReturn(true);
        when(planeRepository.save(updatedPlane)).thenReturn(updatedPlane);

        // act
        Plane result = planeService.updatePlane(updatedPlane);

        // verify
        assertNotNull(result);
        assertEquals(updatedPlane.getPlaneId(), result.getPlaneId());
        verify(planeRepository).existsById(updatedPlane.getPlaneId());
        verify(planeRepository).save(updatedPlane);
    }

    @Test
    void updatePlane_PlaneNotFound_Failure() {
        // arrange
        Plane nonExistentPlane = new Plane("NonExistentPlane", 3, "NonExistent Model");
        when(planeRepository.existsById(nonExistentPlane.getPlaneId())).thenReturn(false);

        // act and verify
        PlaneNotFoundException exception = assertThrows(PlaneNotFoundException.class, () -> {
            planeService.updatePlane(nonExistentPlane);
        });
        assertEquals("Could not find plane " + nonExistentPlane.getPlaneId(), exception.getMessage());
        verify(planeRepository).existsById(nonExistentPlane.getPlaneId());
    }
}
