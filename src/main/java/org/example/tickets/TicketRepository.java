package org.example.tickets;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Ticket} entities.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {}
