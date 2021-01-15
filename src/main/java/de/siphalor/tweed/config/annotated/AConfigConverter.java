package de.siphalor.tweed.config.annotated;

import de.siphalor.tweed.tailor.ClothTailor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AConfigConverter {
    @SuppressWarnings("rawtypes")
    Class<? extends ClothTailor.EntryConverter> type();
    String[] args();
}
