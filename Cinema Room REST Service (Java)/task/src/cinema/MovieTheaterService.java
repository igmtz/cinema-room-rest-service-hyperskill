package cinema;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MovieTheaterService {
    private final int totalRows = 9;
    private final int totalColumns = 9;
    private final List<Seat> availableSeats;
    private final List<PurchaseResponse> tickets;

    public MovieTheaterService() {
        tickets = new ArrayList<>();
//        initializeTickets();
        availableSeats = generateAvailableSeats();
    }

    public MovieTheaterInfo getMovieTheaterInfo() {
        MovieTheaterInfo movieTheaterInfo = new MovieTheaterInfo();
        movieTheaterInfo.setTotalRows(totalRows);
        movieTheaterInfo.setTotalColumns(totalColumns);
        movieTheaterInfo.setAvailableSeats(availableSeats);
        return movieTheaterInfo;
    }

    public PurchaseResponse purchaseTicket(Seat seat) throws SeatAlreadyTakenException, InvalidSeatException, TicketNotFoundException {
        if (isSeatOutOfBounds(seat)) {
            throw new InvalidSeatException("The number of a row or a column is out of bounds!");
        }

        Seat selectedSeat = findSeat(seat.getRow(), seat.getColumn());

        if (selectedSeat == null) {
            throw new SeatAlreadyTakenException("The ticket has already been purchased!");
        }

        if (selectedSeat.isPurchased()) {
            throw new SeatAlreadyTakenException("The ticket has already been purchased!");
        }

        int price = calculateTicketPrice(seat.getRow());
        selectedSeat.setPurchased(true);
        selectedSeat.setPrice(price);
        Ticket newTicket = new Ticket(selectedSeat.getRow(), selectedSeat.getColumn(), price, true);
        PurchaseResponse purchaseResponse = new PurchaseResponse(newTicket);
        tickets.add(purchaseResponse);
        selectedSeat.setPurchased(true);
        availableSeats.remove(selectedSeat);

        return purchaseResponse;
    }

    public TicketDTO returnTicket(String token) throws TicketNotFoundException {
        for (PurchaseResponse purchasedTicket : tickets) {
            System.out.println(purchasedTicket.getToken().toString());
            System.out.println(token);
            if (purchasedTicket.getToken().toString().equalsIgnoreCase(token)) {
                purchasedTicket.getTicket().setPurchased(false);
                tickets.remove(purchasedTicket);
                availableSeats.add(new Seat(purchasedTicket.getTicket().getRow(),
                        purchasedTicket.getTicket().getColumn(),
                        purchasedTicket.getTicket().getPrice()));
                return new TicketDTO(purchasedTicket.getTicket());
            }
        }
        throw new TicketNotFoundException("Wrong token!");
    }

    private List<Seat> generateAvailableSeats() {
        List<Seat> seats = new ArrayList<>();

        for (int row = 1; row <= totalRows; row++) {
            for (int column = 1; column <= totalColumns; column++) {
                int price = calculateTicketPrice(column);
                Seat seat = new Seat(row, column, price);
                seats.add(seat);
            }
        }
        return seats;
    }

    private boolean isSeatOutOfBounds(Seat seat) {
        return seat.getRow() < 1 || seat.getRow() > totalRows || seat.getColumn() < 1 || seat.getColumn() > totalColumns;
    }

    private Seat findSeat(int row, int column) {
        for (Seat seat : availableSeats) {
            if (seat.getRow() == row && seat.getColumn() == column) {
                return seat;
            }
        }
        return null;
    }

    private int calculateTicketPrice(int row) {
        return (row <= 4) ? 10 : 8;
    }

    public Stats getStats() {
        int income = tickets.stream()
                .mapToInt(x -> x.getTicket().getPrice())
                .sum();
        int availableSeats = this.availableSeats.size();
        int purchasedTickets = tickets.size();
        return new Stats(income, availableSeats, purchasedTickets);
    }
}
