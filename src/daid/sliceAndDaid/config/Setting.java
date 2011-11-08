package daid.sliceAndDaid.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting
{
	int LEVEL_STARTER = 0;
	int LEVEL_NORMAL = 1;
	int LEVEL_ADVANCED = 2;
	int LEVEL_HIDDEN = 3;
	
	public String title() default "";
	
	public String description() default "";
	
	public double minValue() default Double.MIN_VALUE;
	
	public double maxValue() default Double.MAX_VALUE;
	
	public int level() default Setting.LEVEL_NORMAL;
	
	public String enumName() default "";
}
