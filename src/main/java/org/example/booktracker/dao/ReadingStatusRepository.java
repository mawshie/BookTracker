package org.example.booktracker.dao;

import org.example.booktracker.domain.ReadingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingStatusRepository extends JpaRepository<ReadingStatus, Integer> {
}
