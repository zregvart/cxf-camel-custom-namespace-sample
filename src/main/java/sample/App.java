package sample;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.CxfEndpointConfigurer;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.AbstractWSDLBasedEndpointFactory;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class App {

    public static void main(final String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(App.class, args);

        final ProducerTemplate template = context.getBean(ProducerTemplate.class);

        template.sendBodyAndHeader("direct:send", new String[] { "Berlin-Tegel", "Germany" },
                CxfConstants.OPERATION_NAME, "GetWeather");
    }

}

@Component
class CxfRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:send").to("cxf:http://www.webservicex.net/globalweather.asmx"//
                + "?wsdlURL=globalweather.wsdl"//
                + "&serviceClass=net.webservicex.GlobalWeatherSoap"//
                + "&serviceName={http://www.webserviceX.NET}GlobalWeather"//
                + "&endpointName={http://www.webserviceX.NET}GlobalWeatherSoap12"//
                + "&cxfEndpointConfigurer=#namespaceConfigurer"//
                + "&loggingFeatureEnabled=true");
    }

}

@Component("namespaceConfigurer")
class NamespaceConfigurer implements CxfEndpointConfigurer {

    @Override
    public void configure(final AbstractWSDLBasedEndpointFactory factoryBean) {
        final JAXBDataBinding jaxb = new JAXBDataBinding();

        final Map<String, String> namespacePrefixes = new HashMap<>();
        namespacePrefixes.put("http://www.webserviceX.NET", "myns");
        jaxb.setNamespaceMap(namespacePrefixes);

        factoryBean.setDataBinding(jaxb);
    }

    @Override
    public void configureClient(final Client client) {
    }

    @Override
    public void configureServer(final Server server) {
    }

}
