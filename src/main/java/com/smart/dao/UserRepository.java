package com.smart.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User,Integer>{
	@Query("select u from User u where u.email= :email")
	public User getUserByUserName(@Param("email") String email);
	
	
	@Query("select u from User u where u.role <> 'ROLE_ADMIN'")
	public List<User> getAllNormalUser();
	
	
	 // ✅ Get all normal users (non-admin)
    @Query("SELECT u FROM User u WHERE u.role <> 'ROLE_ADMIN'")
    List<User> getAllNormalUsers();
    
    
    @Query("select count(u) from User u where u.role <> 'ROLE_ADMIN'")
    long countNormalUsers();
    
    

    // ✅ Count active users
    @Query("select count(u) from User u where u.role <> 'ROLE_ADMIN' and u.enabled = :enabled")
    long countActiveNormalUsers(@Param("enabled") boolean enabled);
    
    @Query("select count(u) from User u where u.role <> 'ROLE_ADMIN' and u.enabled = false")
    long countInactiveNormalUsers();

    @Query("SELECT u FROM User u WHERE u.role <> 'ROLE_ADMIN' ORDER BY u.id DESC")
    List<User> findTop5NormalUsers();
}
