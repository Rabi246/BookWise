package org.example.bookwise.repository;

import org.example.bookwise.model.Book;
import org.example.bookwise.model.Rating;
import org.example.bookwise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Integer> {

    Optional<Rating> findByUserAndBook(User user, Book book);
}