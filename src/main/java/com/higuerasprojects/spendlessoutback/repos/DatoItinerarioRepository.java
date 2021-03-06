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

	@Query(value="SELECT * FROM tdato_itinerario u WHERE u.ubicacion_nombre=?1 AND u.ubicacion_lon=?3 AND u.ubicacion_lat=?2 ORDER BY u.precio_total DESC ", nativeQuery=true)
	List<DatoItinerario> findAllByUbicacionOrderByPrecioDESC(final String pUbicacionNombre, final double pUbicacionLat,
			final double pUbicacionLon);
	@Query(value="SELECT * FROM tdato_itinerario u WHERE u.ubicacion_nombre=?1 AND u.ubicacion_lon=?3 AND u.ubicacion_lat=?2 ORDER BY u.precio_total ASC ", nativeQuery=true)
	List<DatoItinerario> findAllByUbicacionOrderByPrecioASC(final String pUbicacionNombre, final double pUbicacionLat,
			final double pUbicacionLon);
	
	@Query(value="SELECT * FROM tdato_itinerario u WHERE u.id_user=?1 ORDER BY u.precio_total ASC ", nativeQuery=true)
	List<DatoItinerario> findAllByUserIdOrderByPrecioASC(
			final double pIdUser);
	@Query(value="SELECT * FROM tdato_itinerario u WHERE u.id_user=?1 ORDER BY u.precio_total DESC ", nativeQuery=true)
	List<DatoItinerario> findAllByUserIdOrderByPrecioDESC(
			final double pIdUser);


}
