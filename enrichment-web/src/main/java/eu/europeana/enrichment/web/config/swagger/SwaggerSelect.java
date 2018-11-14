package eu.europeana.enrichment.web.config.swagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(value=TYPE)
@Documented
public @interface SwaggerSelect {
    String value() default "";
}
