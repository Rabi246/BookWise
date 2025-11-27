package org.example.bookwise.repository;

import org.example.bookwise.model.Library;
import org.example.bookwise.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface LibraryRepository extends JpaRepository<Library, Integer> {

    List<Library> findByOwner(User owner);
}