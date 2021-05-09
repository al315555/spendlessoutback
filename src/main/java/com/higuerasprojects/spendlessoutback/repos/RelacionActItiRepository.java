/**
 * 
 */
package com.higuerasprojects.spendlessoutback.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.higuerasprojects.spendlessoutback.model.RelacionActIti;

/**
 * @author Ruhimo
 *
 */
public interface RelacionActItiRepository extends JpaRepository<RelacionActIti, Long> {
	
	@Query("SELECT * FROM RelacionActIti u WHERE u.idItinerario=:pIditinerario")
	List<RelacionActIti> findAllByItinerario(final long pIditinerario);
	
}
