package com.yuehuanghun.mybatis.milu.tool;

import java.lang.reflect.InvocationTargetException;

import org.apache.ibatis.reflection.MetaClass;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.exception.OrmRuntimeException;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.IdAttribute;
import com.yuehuanghun.mybatis.milu.metamodel.EntityBuilder;

/**
 * 实体类工具类
 */
public class EntityUtils {

	/**
	 * 获取实体对象的主键值
	 * @param entity 实体对象
	 * @return 主键值
	 */
	public static Object getIdValue(Object entity) {
		Class<? extends Object> entityClass = entity.getClass();

		MetaClass metaClass = MetaClass.forClass(entityClass, EntityBuilder.REFLECTOR_FACTORY);
		try {
			return metaClass.getGetInvoker(getIdAttrName(entityClass)).invoke(entity, new Object[0]);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new OrmRuntimeException(e);
		}
	}
	
	/**
	 * 获取实体类的主键属性名
	 * @param entityClass 实体类
	 * @return 主键属性名
	 */
	public static String getIdAttrName(Class<?> entityClass) {
		Entity model = null;
		for(MiluConfiguration config : MiluConfiguration.getInstances()) {
			model = config.getMetaModel().getEntity(entityClass);
		}
		
		if(model == null) {
			throw new OrmRuntimeException("未知的实体类：" + entityClass.getName());
		}
		
		IdAttribute idAttr = model.getId();
		if(idAttr == null) {
			throw new OrmRuntimeException("实体类未声明主键属性：" + entityClass.getName());
		}
		return idAttr.getName();
	}
}
