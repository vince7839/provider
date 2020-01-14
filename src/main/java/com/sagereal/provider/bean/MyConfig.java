package com.sagereal.provider.bean;

import com.sagereal.provider.converter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import javax.annotation.PostConstruct;

@Configuration
public class MyConfig {

    @Autowired
    private RequestMappingHandlerAdapter adapter;

    @PostConstruct
    public void addConversionConfig() {
        ConfigurableWebBindingInitializer initializer
                = (ConfigurableWebBindingInitializer) adapter.getWebBindingInitializer();
        GenericConversionService service = (GenericConversionService)initializer.getConversionService();
        if (service != null) {
           // service.addConverter(new DateConverter());
        }
    }
}
