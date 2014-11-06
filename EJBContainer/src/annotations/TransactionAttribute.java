package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionAttribute {
	TransactionAttributeType type() default TransactionAttributeType.REQUIRED;
	public static enum TransactionAttributeType { REQUIRED, REQUIRESNEW, NONE };
}
