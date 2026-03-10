package com.example.rces.repository;

import com.example.rces.models.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {

    Machine findByNumber(Integer number);

}
