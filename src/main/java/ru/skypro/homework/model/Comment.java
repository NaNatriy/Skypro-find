package ru.skypro.homework.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    private String text;
    private Instant createdAt = Instant.now();
    @ManyToOne(fetch = FetchType.LAZY)
    private Ads ads;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
}
