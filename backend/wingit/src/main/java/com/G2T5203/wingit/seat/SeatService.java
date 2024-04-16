package com.G2T5203.wingit.seat;

import com.G2T5203.wingit.plane.Plane;
import com.G2T5203.wingit.plane.PlaneNotFoundException;
import com.G2T5203.wingit.plane.PlaneRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {
    private final SeatRepository repo;
    private final PlaneRepository planeRepo;

    public SeatService(SeatRepository repo, PlaneRepository planeRepo) {
        this.repo = repo;
        this.planeRepo = planeRepo;
    }

    public List<SeatSimpleJson> getAllSeatsForPlaneAsSimpleJson(String planeId) {
        return getAllSeatsForPlane(planeId).stream()
                .map(SeatSimpleJson::new)
                .collect(Collectors.toList());
    }

    public List<Seat> getAllSeatsForPlane(String planeId) {
        if (!planeRepo.existsById(planeId)) throw new PlaneNotFoundException(planeId);
        return repo.findAllBySeatPkPlanePlaneId(planeId);
    }


    @Transactional
    public Seat createSeat(SeatSimpleJson newSeatSimpleJson) {
        Optional<Plane> retrievedPlane = planeRepo.findById(newSeatSimpleJson.getPlaneId());
        if (retrievedPlane.isEmpty()) throw new PlaneNotFoundException(newSeatSimpleJson.getPlaneId());

        SeatPk seatPk = new SeatPk(retrievedPlane.get(), newSeatSimpleJson.getSeatNumber());
        boolean alreadyExists = repo.existsById(seatPk);
        if (alreadyExists) throw new SeatBadRequestException("Seat already exists.");

        Seat newSeat = new Seat(
                seatPk,
                newSeatSimpleJson.getSeatClass(),
                newSeatSimpleJson.getPriceFactor());
        return repo.save(newSeat);
    }

    @Transactional
    public void createSeatsForNewPlane(Plane newPlane) {
        if (repo.existsBySeatPkPlanePlaneId(newPlane.getPlaneId()))
            throw new SeatBadRequestException("Plane already has existing seats, can't use create seats for new plane.");

        int numSeats = newPlane.getCapacity();
        if (numSeats >= 30 && numSeats % 6 == 0) {
            List<Seat> seatsToBeCreated = new ArrayList<Seat>(numSeats);

            int numNonEconomySeats = 0;
            // First class (row 1 to 3) 3 rows, 2 seats per row, 6 seats
            for (int row = 1; row <= 3; row++) {
                for (int seatAlphabet = 0; seatAlphabet < 2; seatAlphabet++) {
                    Character seatChar = (char) ('A' + seatAlphabet);
                    String seatNumber = seatChar + String.format("%02d", row);
                    seatsToBeCreated.add(new Seat(new SeatPk(newPlane, seatNumber), "First", 10.0));
                    numNonEconomySeats++;
                }
            }

            // Business class (row 4 to 6) 3 rows, 4 seats per row, 12 seats
            for (int row = 4; row <= 6; row++) {
                for (int seatAlphabet = 0; seatAlphabet < 4; seatAlphabet++) {
                    Character seatChar = (char) ('A' + seatAlphabet);
                    String seatNumber = seatChar + String.format("%02d", row);
                    seatsToBeCreated.add(new Seat(new SeatPk(newPlane, seatNumber), "Business", 3.0));
                    numNonEconomySeats++;
                }
            }

            // Economy class (row 7 onwards), 6 seats per row.
            int numEconomyRows = (numSeats - numNonEconomySeats) / 6;
            int numEconomySeats = 0;
            for (int row = 7; row < 7 + numEconomyRows; row++) {
                for (int seatAlphabet = 0; seatAlphabet < 6; seatAlphabet++) {
                    Character seatChar = (char) ('A' + seatAlphabet);
                    String seatNumber = seatChar + String.format("%02d", row);
                    seatsToBeCreated.add(new Seat(new SeatPk(newPlane, seatNumber), "Economy", 1.0));
                    numEconomySeats++;
                }
            }

            if (numSeats != (numNonEconomySeats + numEconomySeats))
                throw new SeatBadRequestException("Mismtach in number of created Seats to plane capacity");

            repo.saveAll(seatsToBeCreated);
        } else {
            // TODO: A new implementation just rows of 6 until no have...? For now just throw assert.
            throw new SeatBadRequestException("Planes must be capacity >= 30 and multiple of 6");
        }
    }
}
