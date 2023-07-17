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

## CROSS-SITE REQUEST FORGERY (CSRF)
A typical Cross-Site Request Forgery (CSRF or XSRF) attack aims to perform an operation in a web
application on behalf of a user without their explicit consent. In general, it does not directly steal
 the user's identity, but it exploits the user to carry out an action without their will.

<div align="center">
<img src="img_7.png"/>
<img src="img_8.png">
</div>

### Handle CSRF

#### Approach 1
To defeat a CSRF attack, applications need a way to determine if the HTTP request is legitimately generated via the
application's user interface. The best way to achieve this is through a CSRF token. A CSRF token is a secure random token that 
is used to prevent CSRF attacks. 

    ...and().csrf().ignoringRequestMatchers("/contact","/register")
#### Approach 2
    
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    requestHandler.setCsrfRequestAttributeName("_csrf");


## UNDERSTANDING & IMPLEMENTING AUTHORIZATION

### AUTHENTICATION VS AUTHORIZATION
<div align="center">
<table>
<th>Authentication</th>
<th>Authorization</th>
<tr>
<td>The identity of users is checked for providing the access to system.</td>
<td>person's or user's authorities are checked for accessing the resources.</td>
</tr>
<tr>
<td>Authentication done before authorization.</td>
<td>Authorization always happen after authentication.</td>
</tr>
<tr>
<td>It needs usually user's login details.</td>
<td>It needs user's privilege or roles</td>
</tr>
<tr>
<td>401</td>
<td>403</td>
</tr>
</table>
</div>

    public interface GrantedAuthority{
      String getAuthority();
    }

<div align="center">
<img src="img_9.png">
</div>

### CONFIGURING AUTHORITIES INSIDE SPRING

#### Authority
    .authorizeHttpRequests()
      .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
      .requestMatchers("/myBalance").hasAnyAuthority("VIEWACCOUNT","VIEWBALANCE")


#### Role
    .authorizeHttpRequests()
      .requestMatchers("/myAccount").hasRole("USER")
      .requestMatchers("/myBalance").hasAnyRole("USER","ADMIN")
> ROLE should start with `ROLE_` prefix AND ONLY BE USED DB configuration.

### Authority vs Role
<div align="center">
<table>
<th>Authority</th>
<th>Role</th>
<tr>
<td>Authority is like an individual privilege or an action .</td>
<td>Role is a group of privileges/actions  </td>
</tr>
<tr>
<td>Restricting access in a fine-grained manner.</td>
<td>Restricting access in a coarse-grained manner.</td>
</tr>
<tr>
<td>VIEWACCOUNT, VIEWBALANCE</td>
<td>ROLE_ADMIN, ROLE_USER</td>
</tr>
<tr>
<td>401</td>
<td>403</td>
</tr>
</table>
</div>


## Custom Filters

### Servlets and Filters
<div>
<img src="img_10.png">
</div>

In Java web apps, Servlet Container (Web Server) takes care of translating the HTTP messages for Java code
to understand. One of the mostly used servlet containers is Apache Tomcat.\
Servlet Container converts the HTTP messages into ServletRequest and hand over to Servlet method as a parameter.\
Similarly, ServletResponse returns as an output to Servlet Container from Servlet. So everything we write inside Java
web apps are driven by Servlets.

Filters inside Java web applications can be used to intercept each request/response logic. So using the same 
filters, Spring Security enforce security based on our configurations inside a web application.



**Filter interface**

    public interface Filter {
      default void init(FilterConfig filterConfig) throws ServletException {}
      void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException;
      default void destroy() {}
    }


**Custom Filter**

    public class RequestValidationBeforeFilter  implements Filter {
    
        public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";
        private Charset credentialsCharset = StandardCharsets.UTF_8;
    
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;
            String header = req.getHeader(AUTHORIZATION);
            if (header != null) {
                header = header.trim();
                if (StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
                    byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                    byte[] decoded;
                    try {
                        decoded = Base64.getDecoder().decode(base64Token);
                        String token = new String(decoded, credentialsCharset);
                        int delim = token.indexOf(":");
                        if (delim == -1) {
                            throw new BadCredentialsException("Invalid basic authentication token");
                        }
                        String email = token.substring(0, delim);
                        if (email.toLowerCase().contains("test")) {
                            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            return;
                        }
                    } catch (IllegalArgumentException e) {
                        throw new BadCredentialsException("Failed to decode basic authentication token");
                    }
                }
            }
            chain.doFilter(request, response);
        }
    }

**SecurityConfig**

    ...
    .addFilterBefore.......(new RequestValidationBeforeFilter(),BasicAuthenticationFilter.class)
    ....



    public class AuthoritiesLoggingAtFilter implements Filter {
    
        private final Logger LOG =
                Logger.getLogger(AuthoritiesLoggingAtFilter.class.getName());
    
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            LOG.info("Authentication Validation is in progress");
            chain.doFilter(request, response);
        }
    
    }


    ...
    .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)
    ...


## JWT
### Encoded

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
### Decoded
<div align="center">
<img src="img_11.png">
</div>

### VALIDATION OF JWT TOKENS
<div align="center">
<img src="img_12.png">
</div>

### JSESSIONID
JSESSIONID is a cookie, used for session management in the web application for HTTP Protocol.
**Disadvantages of JSESSIONID**\
It is not giving any value for user data.\
It is not secure.

 
### code

    public class JWTTokenGeneratorFilter extends OncePerRequestFilter{
      @Override
      public void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
          SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
          String jwt = Jwts.builder.setIssuer("Medron").setSubject("JWT Token")
            .claim("username",authentication.getName())
            .claim("authorities",populateAuthorities(authentication.getAuthorities()))
            .setIssuedAt(new Date())
            .setExpriration(new Date(( new Date().getTime() + 30000000 ))
            .signWith(key).compact();
 
          response.setHeader(SecurityConstants.JWT_HEADER, jwt);
        }
        filterChain.doFilter(request,response);
      }
    }


    protected boolean shouldNotFilter(){
      return !request.getServletPath().equals("/user");
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection){
      Set<String> authoritiesSet = new HashSet<>();
      for(GrantedAuthority authority:collection){
        authoritiesSet.add(authority.getAuthority());
      }
      return String.join(",", authoritiesSet);
    }












