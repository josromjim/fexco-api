package jrj.fexco.webclient.websocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.websocket.server.ServerEndpointConfig;

/**
 * Context loader for WebSocket end point. Necessary to pass the Spring
 * configuration to the end point.
 * 
 * @author José Romero
 *
 */
public class EndPointSpringConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {

	/**
	 * Spring application context.
	 */
	private static volatile BeanFactory context;

	@Override
	public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
		return context.getBean(clazz);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		EndPointSpringConfigurator.context = applicationContext;
	}
}