package Utils;

import Pojos.DestroyCreatureEffect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleDestroyCreatureEffect {
    DestroyCreatureEffect value() default DestroyCreatureEffect.NONE;
}
