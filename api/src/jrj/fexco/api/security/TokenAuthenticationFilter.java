package jrj.fexco.api.security;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * Authentication filter for controllers and controllers methods annotated as
 * "Secure".
 * 
 * <p>
 * Note: It is important to note that this is just an example. We use always the
 * same token to authenticate the API client. This is a VERY BAD practice. We
 * should generate a brand new and expiring token creating an entry point for
 * that in the API and consumers should call such entry point to generate a
 * valid token.
 * </p>
 * 
 * @author José Romero
 *
 */
@Secure
@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenAuthenticationFilter implements ContainerRequestFilter {

	private static final String REALM = "todoapi";
	private static final String AUTHENTICATION_SCHEME = "Bearer";

	/**
	 * Performs the request filter
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Validate the Authorization header
		if (!isTokenBasedAuthentication(authorizationHeader)) {
			abortWithUnauthorized(requestContext);
			return;
		}

		// Extract the token from the Authorization header
		String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

		try {

			// Validate the token
			validateToken(token);

		} catch (Exception e) {
			abortWithUnauthorized(requestContext);
		}
	}

	/**
	 * Checks if the Authorization header is valid.
	 * 
	 * @param authorizationHeader Authorization header
	 * @return true if it is valid, false otherwise.
	 */
	private boolean isTokenBasedAuthentication(String authorizationHeader) {
		// It must not be null and must be prefixed with "Bearer" plus a whitespace
		// The authentication scheme comparison must be case-insensitive
		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

	/**
	 * Abort the filter chain with a 401 status code response
	 * 
	 * @param requestContext Request context
	 */
	private void abortWithUnauthorized(ContainerRequestContext requestContext) {
		// The WWW-Authenticate header is sent along with the response
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"").build());
	}

	/**
	 * Validates the token passed in the header.
	 * <p>
	 * Note: Here we should extract the token from the tokens set and validate it (existence and expiration).
	 * But we are simplifying the process and we use always the same token (VERY BAD
	 * practice).
	 * </p>
	 * 
	 * @param token Token to validate.
	 * @throws Exception If the token is not valid.
	 */
	private void validateToken(String token) throws Exception {
		if (!token.equals("YXV0aGVudGljYXRpb250b2tlbg==")) {
			throw new NotAuthorizedException("Token not valid", token);
		}
	}
}
