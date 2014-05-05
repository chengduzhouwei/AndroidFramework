package org.zw.android.framework.db.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define The table PRIMARY KEY
 * 
 * @author zhouwei
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ColumnPrimaryKey {
	
	/** Primary Key Type*/
	public enum PrimaryKeyType {/* auto **/AUTO, /* custom **/DEFINE};
	
	PrimaryKeyType Type() ;
}
