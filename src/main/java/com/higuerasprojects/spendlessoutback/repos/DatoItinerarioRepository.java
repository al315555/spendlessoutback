/**
 * 
 */
package com.higuerasprojects.spendlessoutback.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.higuerasprojects.spendlessoutback.model.DatoItinerario;

/**
 * @author Ruhimo
 *
 */
public interface DatoItinerarioRepository extends JpaRepository<DatoItinerario, Long> {

	@Query(value="SELECT * FROM tdato_itinerario u WHERE u.ubicacion_nombre=?1 AND u.ubicacion_lon=?3 AND u.ubicacion_lat=?2 ORDER BY CASE WHEN 'true'=?4 THEN u.precio_total ASC ELSE u.precio_total DESC END ", nativeQuery=true)
	List<DatoItinerario> findAllByUbicacion(final String pUbicacionNombre, final double pUbicacionLat,
			final double pUbicacionLon, final boolean asc);
	@Query(value="SELECT * FROM tdato_itinerario u WHERE u.id_user=?1 ORDER BY CASE WHEN 'true'=?2 THEN u.precio_total ASC ELSE u.precio_total DESC END", nativeQuery=true)
	List<DatoItinerario> findAllByUserId(
			final double pIdUser, final boolean asc);

}
