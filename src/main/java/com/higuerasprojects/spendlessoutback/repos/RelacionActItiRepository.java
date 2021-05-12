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
	
	@Query(value="SELECT * FROM trelacion_act_iti u WHERE u.id_itinerario=?1 ORDER BY u.order ASC", nativeQuery=true)
	List<RelacionActIti> findAllByItinerario(final long pIditinerario);
	
}
