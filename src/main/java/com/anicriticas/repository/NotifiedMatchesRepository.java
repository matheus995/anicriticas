package com.anicriticas.repository;

import com.anicriticas.model.NotifiedMatches;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotifiedMatchesRepository extends JpaRepository<NotifiedMatches, UUID> {
}
