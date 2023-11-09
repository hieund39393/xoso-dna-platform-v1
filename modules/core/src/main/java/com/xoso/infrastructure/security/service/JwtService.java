
package com.xoso.infrastructure.security.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.xoso.user.model.AppUser;
import com.xoso.user.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Date;

@Service
public class JwtService {
	Logger logger = LoggerFactory.getLogger(JwtService.class);

	@Autowired
	private AppUserRepository appUserRepository;

	public static final String ID = "id";
	public static final String CODE = "code";
	public static final String USERNAME = "username";
	public static final String MOBILE_NO = "mobileNo";
	public static final String TIMESTAMP = "timestamp";
	public static final String TYPE = "type";
	public static final int EXPIRE_TIME = 86400000; // 1 day
	public static final int REFRESH_EXPIRATION = 604800000; // 7 day

	public String generateTokenLoginFromUsername(AppUser appUser) {
		return this.generateToken(appUser, EXPIRE_TIME);
	}

	public String generateRefreshToken(AppUser appUser) {
		return this.generateToken(appUser, REFRESH_EXPIRATION);
	}

	private String generateToken(AppUser appUser, int expireTime) {
		String token = null;
		try {
			String secretKey = appUser.getSecretKey();
			if (secretKey == null) {
				StringBuilder newSecretKey = new StringBuilder();
				try {
					SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG", "SUN");
					byte[] randomBytes = new byte[32];
					secureRandomGenerator.nextBytes(randomBytes);
					for (int i = 0; i < randomBytes.length; i++) {
						int decimal = (int) randomBytes[i] & 0xff;

						String hex = Integer.toHexString(decimal);

						newSecretKey.append(hex);
					}
				} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
					e.printStackTrace();
				}
				secretKey = newSecretKey.toString();
				appUser.setSecretKey(secretKey);
				appUserRepository.save(appUser);
			}

			JWSSigner signer = new MACSigner(generateShareSecret(secretKey));

			JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();
			builder.claim(USERNAME, appUser.getUsername());
			builder.expirationTime(generateExpirationDate(expireTime));

			JWTClaimsSet claimsSet = builder.build();
			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

			// Apply the HMAC protection
			signedJWT.sign(signer);

			// Serialize to compact form, produces something like
			// eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
			token = signedJWT.serialize();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}

	public String getUsernameFromToken(String token) throws Exception {
		String username = null;
		String secretKey = null;
		try {
			JWTClaimsSet claims = getClaimsFromToken(token);
			if(claims!=null){
				username = claims.getStringClaim(USERNAME);
			}
			AppUser appUser = null;
			try {
				if(username!=null){
					appUser = appUserRepository.findAppUserByName(username);
				}
			} catch(Exception e) {
				appUser = null;
			}
			if (appUser != null) {
				secretKey = appUser.getSecretKey();
			}
			//logger.info("secretKey_: "+secretKey);
			SignedJWT signedJWT = SignedJWT.parse(token);
			JWSVerifier verifier = new MACVerifier(generateShareSecret(secretKey));
			if (!signedJWT.verify(verifier)) {
				username = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return username;
	}

	private JWTClaimsSet getClaimsFromToken(String token) {
		JWTClaimsSet claims = null;
		try {

			SignedJWT signedJWT = SignedJWT.parse(token);
			claims = signedJWT.getJWTClaimsSet();
			/*
			 * SignedJWT signedJWT = SignedJWT.parse(token); JWSVerifier verifier = new
			 * MACVerifier(generateShareSecret()); if (signedJWT.verify(verifier)) { claims
			 * = signedJWT.getJWTClaimsSet(); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return claims;
	}

	private byte[] generateShareSecret(String secretKey) {
		// Generate 256-bit (32-byte) shared secret
		byte[] sharedSecret = new byte[32];
		sharedSecret = secretKey.getBytes();
		return sharedSecret;
	}

	public Boolean validateToken(String token) throws Exception {
		if (token == null || token.trim().length() == 0) {
			throw new Exception("Invalid");
//			return false;
		}

		String username = getUsernameFromToken(token);

		if (username == null || username.isEmpty()) {
			throw new Exception("Invalid username");
//			return false;
		}
		if (isTokenExpired(token)) {
			throw new Exception("Invalid token. Token has expired");
//			return false;
		}
		return true;
	}

	private Boolean isTokenExpired(String token) {
		Date expiration = getExpirationDateFromToken(token);
		if(expiration == null){
			return false;
		}
		return expiration.before(new Date());
	}

	private Date generateExpirationDate(int expireTime) {
		return new Date(System.currentTimeMillis() + expireTime);
	}

	private Date getExpirationDateFromToken(String token) {
		Date expiration = null;
		JWTClaimsSet claims = getClaimsFromToken(token);
		expiration = claims.getExpirationTime();
		return expiration;
	}



}



