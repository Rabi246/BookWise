package org.example.bookwise.model;

import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name="libraries")
public class Library {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private User owner;

    @ManyToMany
    private List<Book> books;

}
