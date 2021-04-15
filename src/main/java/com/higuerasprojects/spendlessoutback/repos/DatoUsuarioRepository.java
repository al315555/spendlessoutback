/**
 * 
 */
package com.higuerasprojects.spendlessoutback.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.higuerasprojects.spendlessoutback.model.DatoUsuario;

/**
 * @author Ruhimo
 *
 */
public interface DatoUsuarioRepository extends JpaRepository<DatoUsuario, Long> {

	Optional<DatoUsuario> findByEmail(String pEmail);
	@Query("SELECT COUNT(u) FROM DatoUsuario u WHERE u.email=:pEmail")
	int countByEmail(String pEmail);
	
}
