package ar.edu.palermo.devops.tp.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        final ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setFieldMatchingEnabled(true) // Permite mapear sin setters
                .setFieldAccessLevel(AccessLevel.PRIVATE); // Accede a campos privados (como en records)

        return mapper;
    }
}
