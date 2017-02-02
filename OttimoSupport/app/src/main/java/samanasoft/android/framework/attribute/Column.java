package samanasoft.android.framework.attribute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import samanasoft.android.framework.attribute.EnumAttribute.Bool;
import samanasoft.android.framework.attribute.EnumAttribute.DataType;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	public DataType DataType();
	public String Name();
	public String ServerName() default "";
	public Bool IsIdentity() default Bool.FALSE;
	public Bool IsNullable() default Bool.FALSE;
	public Bool IsPrimaryKey() default Bool.FALSE;
}
