package com.isw2.avistamiento_api.repository;

import com.isw2.avistamiento_api.model.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
	
}