package com.medron.basicsecurity.repository;

import com.medron.basicsecurity.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Integer> {

    List<Customer> findByEmail(String email);

}
