# PADAWAN TO MASTER SPRING-SECURITY


With out Lambda DSL

    http.authorizeHttpRequests()
        .requestMatchers("/info").permitAll()
        .requestMatchers("/myBalance).permitAll().and().formLogin().and().httpBasic();

With Lambda DSL

    http.authorizeHttpRequests((requests)-> .requestMatchers("/info").permitAll()
                                            .requestMatchers("/myBalance).permitAll())  .formLogin(Customizer.withDefaults())
                                                                                        .httpBasic(Customizers.withDefaults());

## DEFINING & MANAGING USERS



### User Management interface and classes
#### UserDetailsService
Core interface which loads user-specific data.

    public interface UserDetailsService{
        UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    }
#### UserDetailsManager
An extension of the UserDetailsService which provides the ability to create new users and update existing ones.

    public interface UserDetailsManager extends UserDetailsService {
	    void createUser(UserDetails user);
	    void updateUser(UserDetails user);
	    void deleteUser(String username);
	    void changePassword(String oldPassword, String newPassword);
	    boolean userExists(String username);
    }
UserDetailsManager has 3 implementations.
<div align="center">
<img src="img_1.png">
<img src="img.png">
</div>

> All the above interface and classes use UserDetails and its implementation.

#### InMemoryUserDetailsManager
#####  Approach 1
Instead of defining a single user inside application.properties, as a next step we can define multiple users along with their authorities.

    @Bean
    public InMemoryUserDetailsManager userDetailsService(){
        UserDetails admin = User.withDefault.PasswordEncoder()
            .username("admin")
            .password("1234")
            .authorities("admin")
            .build();
    
        UserDetails user = User.withDefault.PasswordEncoder()
            .username("user")
            .password("1234")
            .authorities("user")
            .build();

        return new InMemoryUserDetailsManager(admin,user);
    }


##### Approach 2
Create a bean of PasswordEncoder separately.

    @Bean
    public InMemoryUserDetailManager userDetailsService(){
        InMemoryUserDetailManager inMemoryUserDetailManager = new InMemoryUserDetailManager();
        UserDetails admin = User.withUsername("admin").password("1234").authorities("admin").build();
        UserDetails user = User.withUsername("user").password("1234").authorities("user").build();
        inMemoryUserDetailManager.createUser(admin);
        inMemoryUserDetailManager.createUser(user);
        return inMemoryUserDetailManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpsPasswordEncoder.getInstance();
    }

#### JdbcUserDetailsManager

    @Bean
    public UserDetailsService userDetailsService(Datasource datasource){
        return new JdbcUserDetailsManager(datasource)
    }

#### CustomUserDetailsManager

    @Service
    @RequiredArgsConstructor
    public class CustomUserDetailsService implements UserDetailsService{
        private final CustomerRepository repository;    
        
        
        @Override
        public UserDetailsService loadUserByUserName(String username){
            String userName = null;
            List<GrantedAuthority> authorities = null;
            Customer customer = repository.findByName(username);
            if(customer == null){,
                throw new UserNotFoundExceptions("User not found");
            }
            authorities.add(new SimpleGrantedAuthority(customer.getRole()));
            return new User(username,customer.getPassword(),authorities)
        }

    }
   


### UserDetails vs Authentication
<div align="center">
<img src="img_2.png"/>
</div>

## Password Management with PasswordEncoders

### Encoding, Encryption, Hashing

#### Encoding
Encoding is defined as the process of converting data from to another and has nothing to do with _cryptography_.\
It ınvolves no secret and completely reversible.\
Encoding can't be used for securing data. Below are the various publicy available algorithms used for encoding.\
ASCII, BASE64, UNICODE

#### Encryption
Encryption is defined as the process of transforming data in such way that guarantees confidentiality.\
To achieve confidentiality, encryption requires the use of a secret which, in cryptographic terms,we call a "key".\
Encryption can be reversible by using decryption with the help of the key.

#### Hashing 
In hashing, data is converted to the hash value using some hashing function.\
Data once hashed is non-reversible. One can not determine the original data from a hash value generated.\
Given some arbitrary data along with the output of a hashing algorithm, obe can verify whether this data matches the original input data without needing to see the original data.

### PasswordEncoder

    public interface PasswordEncoder{
        String encode(CharSequence rawPassword);
        boolean matches(CharSequence rawPassword, String encodePassword);
        default boolean upgradeEncoding(String encodedPassword) {
		    return false;
	    }
    }
_**Implementations**_
<div align="center">
<img src="img_3.png">
</div>


## AuthenticationProvider
The `AuthenticationProvider` in Spring Security takes care of the authentication logic. The default implementation of 
the `AuthenticationProvider` is to delegate the responsibility of finding the user in the system to 
a `UserDetailsService` implementation and PasswordEncoder for password validation. But if we have a custom
authentication requirement that is not fulfilled by Spring Security framework, then we can build our own
authentication logic by implementing `AuthenticationProvider` interface.


It is the responsibility of the `ProviderManager` which is an implementation of AuthenticationManager, 
to check with all the implementation of `AuthenticationProviders` and try to authenticate the user.

    public interface AuthenticationProvider{
        Authentication authenticate(Authentication authentication) throws AuthenticationException;
        boolean supports(Class<?> authentication);
    }

<div>
<img src="img_5.png">
<img src="img_6.png">
</div>

## CROSS-ORIGIN RESOURCE SHARING (CORS)
CORS is a protocol that enables scripts running on a browser client to interact with resources from a
different origin. For example, if a UI aoo wishes to make an API call running on a different
domain, it would be blocked from doing so by default due to CORS. It is a specification from The World Wide Web Consortium (W3C).

Other Origins mean the URL being accessed differs from the location that JS is running from, by having:
* a different scheme (HTTP or HTTPS)
* a different domain
* a different port

### Handle CORS

#### Approach 1
    @CrossOrigin(origins="http://localhost:4200")
    @CrossOrigin(origins="*")

#### Approach 2

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http){
        http.cors().configurationSource(
            new CorsConfigurationSource(){
                @Override
                public CorsConfiguration getCorsConfiguration(){
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                    config.setAllowedMethods(Collections.singletonList("*"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setMaxAge(3600L);
                    return config;
                }
            }
        ).and()...
    }






















