package com.example.rces.repository;

import com.example.rces.models.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReasonRepository extends JpaRepository<Reason, Long> {

    Reason findByText(String text);

    @Query("SELECT r FROM Reason r WHERE LOWER(r.text) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Reason> searchByText(@Param("searchTerm") String searchTerm);

}
