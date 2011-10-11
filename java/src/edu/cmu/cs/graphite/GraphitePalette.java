package edu.cmu.cs.graphite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Java annotation for associating a Graphite palette with a type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GraphitePalette {
	String url();
	String displayString() default "";
	String description() default "";
	int initWidth() default -1;
	int initHeight() default -1;
}
