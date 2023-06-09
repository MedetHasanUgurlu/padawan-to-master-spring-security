package com.medron.basicsecurity.repository;

import com.medron.basicsecurity.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {
    Customer findByEmail(String email);
}
