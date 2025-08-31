package org.example.booktracker.dao;

import org.example.booktracker.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByUserId(int userID);

    List<Book> findByTitleAndUserId(String title, int userId);
}
