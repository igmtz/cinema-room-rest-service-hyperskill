package cinema;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TicketDTO {
    @JsonProperty("returned_ticket")
    private Ticket ticket;

    public TicketDTO(Ticket ticket) {
        this.ticket = ticket;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }
}
