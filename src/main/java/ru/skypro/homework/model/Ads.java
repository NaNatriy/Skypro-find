package ru.skypro.homework.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Ads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pk;
    private String title;
    private String description;
    private Integer price;
    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    private byte[] image;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @OneToMany(mappedBy = "ads", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Comment> comments;
}
