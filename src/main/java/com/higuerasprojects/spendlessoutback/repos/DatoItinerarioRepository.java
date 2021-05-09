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
	
	@Query("SELECT u.id, u.nombre, u.precioTotal, u.radio, u.timeStampCreacion, u.timeStampFrom, u.timeStampTo, u.ubicacionlat, u.ubicacionLon, u.ubicacionNombre FROM DatoItinerario u WHERE u.ubicacionNombre=:pUbicacionNombre AND u.ubicacionLon=:pUbicacionLon AND u.ubicacionlat=:pUbicacionLat")
	List<DatoItinerario> findAllByUbicacion(final String pUbicacionNombre, final double pUbicacionLat, final double pUbicacionLon);
	
}
