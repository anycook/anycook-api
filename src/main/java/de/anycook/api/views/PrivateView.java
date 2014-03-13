package de.anycook.api.views;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.jersey.message.filtering.EntityFiltering;

import java.lang.annotation.*;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EntityFiltering
public @interface PrivateView {
    /**
     * Factory class for creating instances of {@code PrivateView} annotation.
     */
    public static class Factory extends AnnotationLiteral<PrivateView> implements PrivateView {

        private Factory() {
        }

        public static PrivateView get() {
            return new Factory();
        }
    }
}
