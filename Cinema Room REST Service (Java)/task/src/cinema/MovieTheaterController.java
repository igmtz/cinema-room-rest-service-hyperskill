package cinema;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping()
public class MovieTheaterController {

    private final MovieTheaterService movieTheaterService;

    public MovieTheaterController(MovieTheaterService movieTheaterService) {
        this.movieTheaterService = movieTheaterService;
    }

    @GetMapping("/seats")
    public MovieTheaterInfo getMovieTheaterInfo() {
        return movieTheaterService.getMovieTheaterInfo();
    }

    @PostMapping("/purchase")
    public ResponseEntity<Object> purchaseTicket(@RequestBody Seat seat) {
        try {
            PurchaseResponse response = movieTheaterService.purchaseTicket(seat);;
            return ResponseEntity.ok(response);
        } catch (SeatAlreadyTakenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error("The ticket has been already purchased!"));
        } catch (InvalidSeatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error("The number of a row or a column is out of bounds!"));
        } catch (TicketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error("Wrong token!"));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<Object> returnTicket(@RequestBody StringToken token) {
        try {
            TicketDTO ticket = movieTheaterService.returnTicket(token.getToken());
            return ResponseEntity.ok(ticket);
        } catch (TicketNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error("Wrong token!"));
        }
    }

    @PostMapping("/stats")
    public ResponseEntity<Object> getStats(@RequestParam(required = false) String password) {
        if (password != null && password.equals("super_secret")) {
            Stats stats = movieTheaterService.getStats();
            return ResponseEntity.ok().body(stats);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Error("The password is wrong!"));
        }
    }
}
