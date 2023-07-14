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

### InMemoryUserDetailsManager
####  Approach 1
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


#### Approach 2
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

###

![img.png](img.png)

