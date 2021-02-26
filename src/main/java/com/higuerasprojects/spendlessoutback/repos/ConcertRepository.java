/**
 * Repository concert database access 
 */
package com.higuerasprojects.spendlessoutback.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.higuerasprojects.spendlessoutback.model.Concert;

/**
 * @author ruhiguer
 *
 */
public interface ConcertRepository extends JpaRepository<Concert, Long> {

}
