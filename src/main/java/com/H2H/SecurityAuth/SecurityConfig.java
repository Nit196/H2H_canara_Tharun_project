package com.H2H.SecurityAuth;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.H2H.Entity.User;
import com.H2H.Repo.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

////@Value("${Expected_Header}")
////private String Expected_Header;        

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
//
////        @Override
////        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////                auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
////        }
////        

	// remove it later
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
	}

	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			System.out.println("Username is the::::_-------------------------->>>>" + username);
			userDetailsService.updateUserLoginStatus(username, false); // Update logout status
		}
	}

//        
//        // Running code backup below
//                @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http
//                .authorizeRequests()
//                    .antMatchers("/", "/css/**", "/js/**", "/image/**", "/fonts/**").permitAll()
//                    .anyRequest().authenticated()
//                    .and()
//                .formLogin()
//                    .loginPage("/login")
//                    .failureUrl("/login?error=true")
////                    .failureUrl("/login?error=true&errorMsg=YourErrorMessageHere")
//
//                  .defaultSuccessUrl("/ReadCertificate")
//                    .permitAll()
//                    .successHandler((request, response, authentication) -> {
//                        String username = authentication.getName();
//                        userDetailsService.updateUserLoginStatus(username, true); // Update login status
//                        
//                        HttpSession session = request.getSession();
//                        session.setAttribute("username", username);
//
//                        response.sendRedirect("/ReadCertificate");                    })
//                    .and()
//                .logout()
//                    .logoutUrl("/H2H/logout")
//                    .logoutSuccessHandler((request, response, authentication) -> {
//                        String username = authentication.getName();
//                        userDetailsService.updateUserLoginStatus(username, false); // Update logout status
//                        response.sendRedirect("/H2H/login");
//                    })
//                    .invalidateHttpSession(true)
//                    .deleteCookies("JSESSIONID")
//                    .permitAll()
//                    .and()
//                .csrf().disable();
//                }
//                

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/css/**", "/js/**", "/image/**", "/fonts/**", "/login", "/login_from_nach",
						"/adminpage")
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login")
				.failureUrl("/login?error=true").defaultSuccessUrl("/ReadCertificate").permitAll()
				.successHandler((request, response, authentication) -> {
					String username = authentication.getName();
					userDetailsService.updateUserLoginStatus(username, true);
					HttpSession session = request.getSession();
					session.setAttribute("username", username);
					response.sendRedirect("/H2H/ReadCertificate");
				}).and().logout().logoutUrl("/H2H/logout").logoutSuccessHandler((request, response, authentication) -> {
					if (authentication != null) {
						String username = authentication.getName();
						userDetailsService.updateUserLoginStatus(username, false);
					}
					response.sendRedirect("/H2H/login");
				}).invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll().and().csrf().disable();
	}

	// Bean definition for ConcurrentSessionControlAuthenticationStrategy
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	// Configure ConcurrentSessionControlAuthenticationStrategy
	@Bean
	public ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlAuthenticationStrategy() {
		return new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

////        @Bean
////        public HttpSessionEventPublisher httpSessionEventPublisher()
////        {
////                return new HttpSessionEventPublisher();
////        }
//
//        
//        
//        
	// Remove it later
	@Autowired

	private UserRepository userRepository;

	// Inside your login method

	@Bean
	public UserDetailsService userDetailsService() {

		return username -> {
			User user = userRepository.findByUsername(username);
			if (user == null) {
				System.out.println("User not found with given username.........................>>>>>>>>>>>>>>>>>>>");
				throw new UsernameNotFoundException("User not found");
			}
			return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
					.password(user.getPassword()).authorities("ROLE_ADMIN")
////                .accountLocked(!user.isLoggedIn()) // Check if user is already logged in
					.accountLocked(user.isLoggedIn()) // Check if user is already logged in
					.build();

		};
	}

	// remote it later
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	////////////////////////////////////

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
		ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean = new ServletListenerRegistrationBean<>();
		listenerRegBean.setListener(new HttpSessionListener() {
			@Override
			public void sessionCreated(HttpSessionEvent se) {
				se.getSession().setMaxInactiveInterval(30 * 60); // Set session timeout to 2 minutes
			}

			@Override
			public void sessionDestroyed(HttpSessionEvent se) {
				System.out.println("Function triggered on session expiry>>>>>>>>>>>>>..");

				try {
					HttpSession session = se.getSession();
					String username = (String) session.getAttribute("username");

					if (username != null) {
						System.out.println("Username in the session destroyed function is:---->" + username);
						userDetailsService.updateUserLoginStatus(username, false); // Update logout status
					} else {
						System.out
								.println("-------------- user is not authenticated, username is null ----------------");
					}
				} catch (Exception e) {
					System.out.println("Exception while updating the flag: " + e.getMessage());
				}
			}

		});
		return listenerRegBean;
	}

	///////////////////////////////////

}
