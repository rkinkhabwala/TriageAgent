package org.example.tickets;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity representing an incident triage ticket.
 */
@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String service;
    private String environment;

    @Column(length = 4000)
    private String summary;

    @Column(length = 12000)
    private String details;
}

