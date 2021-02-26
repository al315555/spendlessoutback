/**
 *  Repository concert database access 
 */
package com.higuerasprojects.spendlessoutback.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.higuerasprojects.spendlessoutback.model.Artist;

/**
 * @author ruhiguer
 *
 */
public interface ArtistRepository extends JpaRepository<Artist, Long> {

}
