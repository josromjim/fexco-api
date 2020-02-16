package jrj.fexco.webclient.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Spring to load the class @see EndPointSpringConfigurator WebSocket
 * end point configurator as a bean.
 * 
 * <p>
 * Note: We can configure it on "WEB-INF/webclient-servlet.xml" with:
 * 
 * <pre>
 * {@code
 * <beans:bean id="customSpringConfigurator"
 *		class="jrj.fexco.webclient.websocket.EndPointSpringConfigurator" />
 * }
 * </pre>
 * 
 * But we use this class to show how to configure it using Annotations.
 * </p>
 * 
 * @author José Romero
 *
 */
@Configuration
public class WebSocketConfiguration {

	@Bean
	public EndPointSpringConfigurator customSpringConfigurator() {
		return new EndPointSpringConfigurator(); // This is just to get context
	}
}
