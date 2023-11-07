package com.example.oauth.imgur.client.controller;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.oauth.imgur.client.configuration.TokenManager;
import com.example.oauth.imgur.client.entity.AppUser;
import com.example.oauth.imgur.client.exception.UserAlreadyExistsException;
import com.example.oauth.imgur.client.service.UserService;
import com.example.oauth.imgur.client.service.UserServiceImpl;

@RequestMapping("/v1/image")
@RestController
public class ImageController {
	
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ImageController.class);

	public static final String AUTHORIZATION = "Authorization";

	@Value("${client.registration.name}")
	private String clientRegistrationName;

	@Value("${spring.security.oauth2.client.registration.app.client-id}")
	private String clientId;

	@Value("${access.token}")
	private String token;

	@Value("${resource.server.url}")
	private String resourceServerURL;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UserService userService;

	@GetMapping("/verifyemail/{emailid}")
	public String verifyEmail(@PathVariable("emailid") String emailid)
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		restTemplate.setRequestFactory(getRequestFactory());
		String url = resourceServerURL+"/account/" + emailid + "/verifyemail";
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, "Bearer " + token);
		HttpEntity<Void> request = new HttpEntity<>(headers);
		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, request, Object.class);
		return "The response is : " + response.getBody().toString();

	}
	
	@DeleteMapping("/{username}/{deleteHash}")
	public ResponseEntity<Object> deleteImage(@PathVariable("deleteHash") String deleteHash,@PathVariable("username") String username) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		log.info("Deleting image with deleteHash : "+deleteHash);
		if(this.userService.findByUsername(username)  == null) {
			log.error("User Not Registered with User ID : " +username);
			throw new UsernameNotFoundException("User Not Registered with User ID : " +username +". Please register first.");
		}
		restTemplate.setRequestFactory(getRequestFactory());
		String url = resourceServerURL+"/image/" + deleteHash;
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, "Bearer " + token);
		HttpEntity<Void> request = new HttpEntity<>(headers);
		ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.DELETE, request, Object.class);
		return response;
	}
	
	@PostMapping("/save/{username}")
	public ResponseEntity<Object>  uploadImage(@RequestParam("file") MultipartFile file,@PathVariable("username") String username) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, IOException {
		log.info("Saving image for user: "+username);
		if(this.userService.findByUsername(username)  == null) {
			throw new UsernameNotFoundException("User Not Registered with User ID : " +username +". Please register first.");
		}
			String url = resourceServerURL+"/upload";
			HttpHeaders headers = new HttpHeaders();
			headers.add(AUTHORIZATION, "Bearer " + token);
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("file", file.getBytes());
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Object> response = restTemplate
			  .postForEntity(url, requestEntity, Object.class);
		return response;
		
	}

	@PostMapping("/register")
	public ResponseEntity<AppUser> registerUser(@RequestBody AppUser user) {
		System.out.println("Inside Controller for user : " + user.getName());
		if(this.userService.findByUsername(user.getUsername()) != null) {
			throw new UserAlreadyExistsException("User already exists with the given ID.");
		}
		return ResponseEntity.ok().body(this.userService.registerUser(user));
	}
	
	@GetMapping("/{username}/{imageHash}")
		public ResponseEntity<Object>  getImage(@PathVariable("username") String username,
				@PathVariable("imageHash") String imageHash) 
						throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		restTemplate.setRequestFactory(getRequestFactory());
			if(this.userService.findByUsername(username)  == null) {
				throw new UsernameNotFoundException("User Not Registered with User ID : " +username +". Please register first.");
			}
				String url = resourceServerURL+"/image/" +imageHash;
				HttpHeaders headers = new HttpHeaders();
				headers.add(AUTHORIZATION, "Bearer " + token);
				HttpEntity<Void> request = new HttpEntity<>(headers);
				ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, request, Object.class);
				return ResponseEntity.ok().body(response.getBody());
		}
		
	
	public HttpComponentsClientHttpRequestFactory getRequestFactory()
			throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
		SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy)
				.build();
		SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setHttpClient(httpClient);
		return requestFactory;
	}
	

}
