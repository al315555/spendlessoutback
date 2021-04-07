/**
 * 
 */
package com.higuerasprojects.spendlessoutback.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.higuerasprojects.spendlessoutback.model.DatoUsuario;

/**
 * @author Ruhimo
 *
 */
public interface DatoUsuarioRepository extends JpaRepository<DatoUsuario, Long> {

	Optional<DatoUsuario> findByEmail(String pEmail);
	
}
