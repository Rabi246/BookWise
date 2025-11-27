package org.example.bookwise.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ratings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Book book;

    // between 1 and 5
    private int value;

    public Rating() {}

    public Rating(User user, Book book, int value) {
        this.user = user;
        this.book = book;
        this.value = value;
    }

    public Integer getId() { return id; }
    public User getUser() { return user; }
    public Book getBook() { return book; }
    public int getValue() { return value; }

    public void setValue(int value) { this.value = value; }
}
