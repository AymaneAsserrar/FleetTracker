package com.example.backend.config;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.locationtech.jts.geom.Geometry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fleetTrackerOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("FleetTracker API")
                        .description("REST API for managing fleet vehicles, routes, trips, and real-time location tracking")
                        .version("1.0.0")
                        .contact(new Contact().name("FleetTracker Team")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName, new SecurityScheme()
                                .name(schemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    /** Prevents SpringDoc from crashing on JTS Geometry/Point types. */
    @Bean
    public ModelConverter jtsGeometryConverter() {
        return new ModelConverter() {
            @Override
            public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
                if (type.getType() instanceof Class<?> cls && Geometry.class.isAssignableFrom(cls)) {
                    return new ObjectSchema()
                            .name(cls.getSimpleName())
                            .description("Geographic point")
                            .addProperty("longitude", new NumberSchema().description("Longitude"))
                            .addProperty("latitude", new NumberSchema().description("Latitude"));
                }
                return chain.hasNext() ? chain.next().resolve(type, context, chain) : null;
            }
        };
    }
}
