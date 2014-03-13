package de.anycook.api.views;

import org.glassfish.hk2.api.AnnotationLiteral;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public @interface TasteNumView {
    /**
     * Factory class for creating instances of {@code TasteNumView} annotation.
     */
    public static class Factory extends AnnotationLiteral<TasteNumView> implements TasteNumView {

        private Factory() {
        }

        public static TasteNumView get() {
            return new Factory();
        }
    }
}
