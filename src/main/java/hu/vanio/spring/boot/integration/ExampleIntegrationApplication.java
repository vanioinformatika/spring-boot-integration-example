package hu.vanio.spring.boot.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

@Configuration
@EnableAutoConfiguration
@ComponentScan
public class ExampleIntegrationApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExampleIntegrationApplication.class, args);
    }
    
    @Bean
    public ServletRegistrationBean wsDispatcherServlet() {
        MessageDispatcherServlet wsDispatcherServlet = new MessageDispatcherServlet();
        wsDispatcherServlet.setTransformWsdlLocations(true);
        ServletRegistrationBean servletDef = new ServletRegistrationBean(wsDispatcherServlet, "/contentStore", "*.wsdl");
        servletDef.addInitParameter("contextConfigLocation", "classpath:integration-context.xml");
        servletDef.setLoadOnStartup(1);
        return servletDef;
    }
    
    @Bean
    public WsdlDefinition contentStore() {
        return new SimpleWsdl11Definition(new ClassPathResource("/contentStore.wsdl"));
    }
    
    @Bean
    public Object marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller(); 
        marshaller.setMtomEnabled(true);
        marshaller.setContextPath("hu.vanio.springwsmtom.wstypes");
        return marshaller;
    }

}
