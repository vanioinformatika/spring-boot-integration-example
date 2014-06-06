package hu.vanio.spring.boot.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:/integration-context.xml")
public class ExampleIntegrationApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExampleIntegrationApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean wsDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet wsDispatcherServlet = new MessageDispatcherServlet();
        wsDispatcherServlet.setApplicationContext(context);
        wsDispatcherServlet.setTransformWsdlLocations(true);
        ServletRegistrationBean servletDef = new ServletRegistrationBean(wsDispatcherServlet, "/contentStore", "*.wsdl");
        servletDef.setLoadOnStartup(1);
        return servletDef;
    }

    @Bean
    public WsdlDefinition contentStore() {
        return new SimpleWsdl11Definition(new ClassPathResource("/contentStore.wsdl"));
    }

    @Bean
    public XsdSchema contentStoreSchema() {
        SimpleXsdSchema xsdSchema = new SimpleXsdSchema();
        xsdSchema.setXsd(new ClassPathResource("contentStoreSchema.xsd"));
        return xsdSchema;
    }

    @Bean
    public Object marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setMtomEnabled(true);
        marshaller.setContextPath("hu.vanio.springwsmtom.wstypes");
        return marshaller;
    }

    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }
            
    @Order(Ordered.LOWEST_PRECEDENCE - 8)
    public static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/contentStore", "*.wsdl").permitAll();
        }
    }

}
