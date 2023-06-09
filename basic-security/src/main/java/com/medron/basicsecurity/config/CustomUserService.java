package com.medron.basicsecurity.config;

import com.medron.basicsecurity.entity.Customer;
import com.medron.basicsecurity.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {
    private final CustomerRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (repository.findByEmail(username) == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Customer customer = repository.findByEmail(username);
        authorities.add(new SimpleGrantedAuthority(customer.getRole()));
        return new User(customer.getEmail(), customer.getPassword(), authorities);
    }
}
