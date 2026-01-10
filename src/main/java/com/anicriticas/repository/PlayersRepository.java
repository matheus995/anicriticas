package com.anicriticas.repository;

import com.anicriticas.model.Players;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayersRepository extends JpaRepository<Players, UUID> {
}
