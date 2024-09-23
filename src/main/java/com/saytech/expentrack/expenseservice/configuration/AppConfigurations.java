package com.saytech.expentrack.expenseservice.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigurations {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
