package com.yuehuanghun.mybatismilu.spring.boot.autoconfigure;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.AutoMappingUnknownColumnBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * MyBatis配置属性
 */
@ConfigurationProperties(prefix = MybatisProperties.MYBATIS_PREFIX)
public class MybatisProperties {

	public static final String MYBATIS_PREFIX = "mybatis";

	private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

	/**
	 * MyBatis xml 配置文件位置
	 */
	private String configLocation;

	/**
	 * MyBatis mapper 文件位置
	 */
	private String[] mapperLocations;

	/**
	 * 使用别名的类所在包 (多个包可使用 ,; \t\n作为分隔符)
	 */
	private String typeAliasesPackage;

	/**
	 * 使用别名的类的超类
	 */
	private Class<?> typeAliasesSuperType;

	/**
	 * type handler实现类所在包 (多个包可使用 ,; \t\n作为分隔符)
	 */
	private String typeHandlersPackage;

	/**
	 * 是否检查MyBatis xml 配置文件是否存在
	 */
	private boolean checkConfigLocation = false;

	/**
	 * 默认执行器类型 {@link org.mybatis.spring.SqlSessionTemplate}.
	 */
	private ExecutorType executorType;

	/**
	 * 默认的脚本语言驱动类
	 */
	private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

	/**
	 * 配置
	 */
	private Properties configurationProperties;

	/**
	 * Configuration的自定义配置。如果 {@link #configLocation}有值, 则此配置不会被使用
	 */
	private CoreConfiguration configuration;

	/**
	 * 标识符使用引号包装
	 */
	private boolean identifierWrapQuote = true;

	/**
	 * 自动创建实体类的ResultMap
	 */
	private boolean createEntityResultMap;

	/**
	 * 自动设置字段的jdbcType
	 */
	private boolean autoSetupColumnJdbcType = true;

	public String getConfigLocation() {
		return this.configLocation;
	}

	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	public String[] getMapperLocations() {
		return this.mapperLocations;
	}

	public void setMapperLocations(String[] mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	public String getTypeHandlersPackage() {
		return this.typeHandlersPackage;
	}

	public void setTypeHandlersPackage(String typeHandlersPackage) {
		this.typeHandlersPackage = typeHandlersPackage;
	}

	public String getTypeAliasesPackage() {
		return this.typeAliasesPackage;
	}

	public void setTypeAliasesPackage(String typeAliasesPackage) {
		this.typeAliasesPackage = typeAliasesPackage;
	}

	public Class<?> getTypeAliasesSuperType() {
		return typeAliasesSuperType;
	}

	public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
		this.typeAliasesSuperType = typeAliasesSuperType;
	}

	public boolean isCheckConfigLocation() {
		return this.checkConfigLocation;
	}

	public void setCheckConfigLocation(boolean checkConfigLocation) {
		this.checkConfigLocation = checkConfigLocation;
	}

	public ExecutorType getExecutorType() {
		return this.executorType;
	}

	public void setExecutorType(ExecutorType executorType) {
		this.executorType = executorType;
	}

	public Class<? extends LanguageDriver> getDefaultScriptingLanguageDriver() {
		return defaultScriptingLanguageDriver;
	}

	public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
		this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
	}

	public Properties getConfigurationProperties() {
		return configurationProperties;
	}

	public void setConfigurationProperties(Properties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	public CoreConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(CoreConfiguration configuration) {
		this.configuration = configuration;
	}

	public Resource[] resolveMapperLocations() {
		return Stream.of(Optional.ofNullable(this.mapperLocations).orElse(new String[0]))
				.flatMap(location -> Stream.of(getResources(location))).toArray(Resource[]::new);
	}

	private Resource[] getResources(String location) {
		try {
			return resourceResolver.getResources(location);
		} catch (IOException e) {
			return new Resource[0];
		}
	}

	public boolean isIdentifierWrapQuote() {
		return identifierWrapQuote;
	}

	public void setIdentifierWrapQuote(boolean identifierWrapQuote) {
		this.identifierWrapQuote = identifierWrapQuote;
	}

	public boolean isCreateEntityResultMap() {
		return createEntityResultMap;
	}

	public void setCreateEntityResultMap(boolean createEntityResultMap) {
		this.createEntityResultMap = createEntityResultMap;
	}

	public boolean isAutoSetupColumnJdbcType() {
		return autoSetupColumnJdbcType;
	}

	public void setAutoSetupColumnJdbcType(boolean autoSetupColumnJdbcType) {
		this.autoSetupColumnJdbcType = autoSetupColumnJdbcType;
	}

	/**
	 * mybatis configuration 核心属性配置
	 *
	 */
	public static class CoreConfiguration {

		/**
		 * 开启RowBounds安全模式。如果允许对嵌套statement使用RowBounds，设置为false。默认值为false
		 */
		private Boolean safeRowBoundsEnabled;

		/**
		 * 开启ResultHandler安全模式。如果允许对嵌套statement使用ResultHandler，设置为false。默认值为false
		 */
		private Boolean safeResultHandlerEnabled;

		/**
		 * 开启数据表下划线模式的字段自动转为小驼峰模式的java属性。默认值为false
		 */
		private Boolean mapUnderscoreToCamelCase;

		/**
		 * 启用后，任何方法调用都将加载对象的所有惰性属性。否则按需加载（参考lazyLoadTriggerMethods）。默认值为false。
		 */
		private Boolean aggressiveLazyLoading;

		/**
		 * 是否允许一个statement返回多个ResultSets (需求兼容的驱动)。默认值为true。
		 */
		private Boolean multipleResultSetsEnabled;

		/**
		 * 允许JDBC支持的主键生成功能。需要兼容的驱动支持。默认值为false。
		 */
		private Boolean useGeneratedKeys;

		/**
		 * 使用列标签而不是列名。不同的驱动有不同表现。可参考驱动的文档，或者测试这两种模式以确定驱动的行为。默认值为true。
		 */
		private Boolean useColumnLabel;

		/**
		 * 开启全局缓存。默认值为true.
		 */
		private Boolean cacheEnabled;

		/**
		 * 当查询到的值为null时，是否调用setter或Map的put方法。int、boolean等基础属性不会设置为null。默认值为false。
		 */
		private Boolean callSettersOnNulls;

		/**
		 * 使用方法形参名作为查询参数的变量。要使用此特性，项目必须使用带有-parameters选项的Java 8进行编译。默认值为true。
		 */
		private Boolean useActualParamName;

		/**
		 * 默认情况下，当返回行的所有列都为null时，MyBatis会返回null。当设置为true时，MyBatis返回一个空实例。同样适用于嵌套结果集：collection或association。默认值为false。
		 */
		private Boolean returnInstanceForEmptyRow;

		/**
		 * 删除SQL中多余的空白字符，但SQL中的字符串查询值也会受到影响. 默认值为false
		 */
		private Boolean shrinkWhitespacesInSql;

		/**
		 * 在“foreach”标签上指定“nullable”属性的默认值. 默认值为false<br>
		 * 即<foreach nullable=指定值>
		 */
		private Boolean nullableOnForEach;

		/**
		 * 使用构建函数自动映射时，使用变更名为匹配方式（默认按字段顺序匹配）。默认值为false。
		 */
		private Boolean argNameBasedConstructorAutoMapping;

		/**
		 * 开启全局懒加载。默认值为false。
		 */
		private Boolean lazyLoadingEnabled;

		/**
		 * 默认查询的超时时间。单位秒。
		 */
		private Integer defaultStatementTimeout;

		/**
		 * 默认查询数据条数
		 */
		private Integer defaultFetchSize;

		/**
		 * 缓存作用域
		 */
		private LocalCacheScope localCacheScope;

		/**
		 * 指定null值的默认jdbcType。
		 */
		private JdbcType jdbcTypeForNull;

		/**
		 * 默认的结果集滚动方式
		 */
		private ResultSetType defaultResultSetType;

		/**
		 * 默认的执行器类型。默认值为SIMPLE.
		 */
		private ExecutorType defaultExecutorType;

		/**
		 * 指定MyBatis是否以及如何自动将列映射到字段/属性。NONE禁用自动映射。<br>
		 * PARTIAL将仅自动映射内部未定义嵌套结果映射的结果集。 FULL将自动映射结果任何复杂度的映射（包含嵌套或其他）。默认值为PARTIAL
		 */
		private AutoMappingBehavior autoMappingBehavior;

		/**
		 * 指定自动映射未知字段或属性的行为。默认值为NONE。
		 */
		private AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior;

		/**
		 * 指定logger日志名前缀.
		 */
		private String logPrefix;

		/**
		 * 指定对象哪里方法使用懒加载. 默认值为 [equals,clone,hashCode,toString].
		 */
		private Set<String> lazyLoadTriggerMethods;

		/**
		 * 指定日志的实现类。
		 */
		private Class<? extends Log> logImpl;

		/**
		 * 指定VFS的实现类.
		 */
		private Class<? extends VFS> vfsImpl;

		/**
		 * 指定默认的SqlProvider类
		 */
		private Class<?> defaultSqlProviderType;

		/**
		 * 指定默认的枚举类型处理器：TypeHandler的实现
		 */
		@SuppressWarnings("rawtypes")
		Class<? extends TypeHandler> defaultEnumTypeHandler;

		/**
		 * 指定提供Configuration实例的类。<br>
		 * 返回的Configuration实例用于加载反序列化对象的懒加载属性<br>
		 * 该类必有一个静态方式：Configuration getConfiguration()
		 */
		private Class<?> configurationFactory;

		/**
		 * 指定任意的 configuration 变量.
		 */
		private Properties variables;

		/**
		 * 指定要切换查询使用的数据库标识。
		 */
		private String databaseId;

		public Boolean getSafeRowBoundsEnabled() {
			return safeRowBoundsEnabled;
		}

		public void setSafeRowBoundsEnabled(Boolean safeRowBoundsEnabled) {
			this.safeRowBoundsEnabled = safeRowBoundsEnabled;
		}

		public Boolean getSafeResultHandlerEnabled() {
			return safeResultHandlerEnabled;
		}

		public void setSafeResultHandlerEnabled(Boolean safeResultHandlerEnabled) {
			this.safeResultHandlerEnabled = safeResultHandlerEnabled;
		}

		public Boolean getMapUnderscoreToCamelCase() {
			return mapUnderscoreToCamelCase;
		}

		public void setMapUnderscoreToCamelCase(Boolean mapUnderscoreToCamelCase) {
			this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
		}

		public Boolean getAggressiveLazyLoading() {
			return aggressiveLazyLoading;
		}

		public void setAggressiveLazyLoading(Boolean aggressiveLazyLoading) {
			this.aggressiveLazyLoading = aggressiveLazyLoading;
		}

		public Boolean getMultipleResultSetsEnabled() {
			return multipleResultSetsEnabled;
		}

		public void setMultipleResultSetsEnabled(Boolean multipleResultSetsEnabled) {
			this.multipleResultSetsEnabled = multipleResultSetsEnabled;
		}

		public Boolean getUseGeneratedKeys() {
			return useGeneratedKeys;
		}

		public void setUseGeneratedKeys(Boolean useGeneratedKeys) {
			this.useGeneratedKeys = useGeneratedKeys;
		}

		public Boolean getUseColumnLabel() {
			return useColumnLabel;
		}

		public void setUseColumnLabel(Boolean useColumnLabel) {
			this.useColumnLabel = useColumnLabel;
		}

		public Boolean getCacheEnabled() {
			return cacheEnabled;
		}

		public void setCacheEnabled(Boolean cacheEnabled) {
			this.cacheEnabled = cacheEnabled;
		}

		public Boolean getCallSettersOnNulls() {
			return callSettersOnNulls;
		}

		public void setCallSettersOnNulls(Boolean callSettersOnNulls) {
			this.callSettersOnNulls = callSettersOnNulls;
		}

		public Boolean getUseActualParamName() {
			return useActualParamName;
		}

		public void setUseActualParamName(Boolean useActualParamName) {
			this.useActualParamName = useActualParamName;
		}

		public Boolean getReturnInstanceForEmptyRow() {
			return returnInstanceForEmptyRow;
		}

		public void setReturnInstanceForEmptyRow(Boolean returnInstanceForEmptyRow) {
			this.returnInstanceForEmptyRow = returnInstanceForEmptyRow;
		}

		public Boolean getShrinkWhitespacesInSql() {
			return shrinkWhitespacesInSql;
		}

		public void setShrinkWhitespacesInSql(Boolean shrinkWhitespacesInSql) {
			this.shrinkWhitespacesInSql = shrinkWhitespacesInSql;
		}

		public Boolean getNullableOnForEach() {
			return nullableOnForEach;
		}

		public void setNullableOnForEach(Boolean nullableOnForEach) {
			this.nullableOnForEach = nullableOnForEach;
		}

		public Boolean getArgNameBasedConstructorAutoMapping() {
			return argNameBasedConstructorAutoMapping;
		}

		public void setArgNameBasedConstructorAutoMapping(Boolean argNameBasedConstructorAutoMapping) {
			this.argNameBasedConstructorAutoMapping = argNameBasedConstructorAutoMapping;
		}

		public String getLogPrefix() {
			return logPrefix;
		}

		public void setLogPrefix(String logPrefix) {
			this.logPrefix = logPrefix;
		}

		public Class<? extends Log> getLogImpl() {
			return logImpl;
		}

		public void setLogImpl(Class<? extends Log> logImpl) {
			this.logImpl = logImpl;
		}

		public Class<? extends VFS> getVfsImpl() {
			return vfsImpl;
		}

		public void setVfsImpl(Class<? extends VFS> vfsImpl) {
			this.vfsImpl = vfsImpl;
		}

		public Class<?> getDefaultSqlProviderType() {
			return defaultSqlProviderType;
		}

		public void setDefaultSqlProviderType(Class<?> defaultSqlProviderType) {
			this.defaultSqlProviderType = defaultSqlProviderType;
		}

		public LocalCacheScope getLocalCacheScope() {
			return localCacheScope;
		}

		public void setLocalCacheScope(LocalCacheScope localCacheScope) {
			this.localCacheScope = localCacheScope;
		}

		public JdbcType getJdbcTypeForNull() {
			return jdbcTypeForNull;
		}

		public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
			this.jdbcTypeForNull = jdbcTypeForNull;
		}

		public Set<String> getLazyLoadTriggerMethods() {
			return lazyLoadTriggerMethods;
		}

		public void setLazyLoadTriggerMethods(Set<String> lazyLoadTriggerMethods) {
			this.lazyLoadTriggerMethods = lazyLoadTriggerMethods;
		}

		public Integer getDefaultStatementTimeout() {
			return defaultStatementTimeout;
		}

		public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
			this.defaultStatementTimeout = defaultStatementTimeout;
		}

		public Integer getDefaultFetchSize() {
			return defaultFetchSize;
		}

		public void setDefaultFetchSize(Integer defaultFetchSize) {
			this.defaultFetchSize = defaultFetchSize;
		}

		public ResultSetType getDefaultResultSetType() {
			return defaultResultSetType;
		}

		public void setDefaultResultSetType(ResultSetType defaultResultSetType) {
			this.defaultResultSetType = defaultResultSetType;
		}

		public ExecutorType getDefaultExecutorType() {
			return defaultExecutorType;
		}

		public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
			this.defaultExecutorType = defaultExecutorType;
		}

		public AutoMappingBehavior getAutoMappingBehavior() {
			return autoMappingBehavior;
		}

		public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
			this.autoMappingBehavior = autoMappingBehavior;
		}

		public AutoMappingUnknownColumnBehavior getAutoMappingUnknownColumnBehavior() {
			return autoMappingUnknownColumnBehavior;
		}

		public void setAutoMappingUnknownColumnBehavior(
				AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior) {
			this.autoMappingUnknownColumnBehavior = autoMappingUnknownColumnBehavior;
		}

		public Properties getVariables() {
			return variables;
		}

		public void setVariables(Properties variables) {
			this.variables = variables;
		}

		public Boolean getLazyLoadingEnabled() {
			return lazyLoadingEnabled;
		}

		public void setLazyLoadingEnabled(Boolean lazyLoadingEnabled) {
			this.lazyLoadingEnabled = lazyLoadingEnabled;
		}

		public Class<?> getConfigurationFactory() {
			return configurationFactory;
		}

		public void setConfigurationFactory(Class<?> configurationFactory) {
			this.configurationFactory = configurationFactory;
		}

		@SuppressWarnings({ "rawtypes", })
		public Class<? extends TypeHandler> getDefaultEnumTypeHandler() {
			return defaultEnumTypeHandler;
		}

		@SuppressWarnings("rawtypes")
		public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> defaultEnumTypeHandler) {
			this.defaultEnumTypeHandler = defaultEnumTypeHandler;
		}

		public String getDatabaseId() {
			return databaseId;
		}

		public void setDatabaseId(String databaseId) {
			this.databaseId = databaseId;
		}

		public void applyTo(Configuration target) {
			PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			mapper.from(getSafeRowBoundsEnabled()).to(target::setSafeRowBoundsEnabled);
			mapper.from(getSafeResultHandlerEnabled()).to(target::setSafeResultHandlerEnabled);
			mapper.from(getMapUnderscoreToCamelCase()).to(target::setMapUnderscoreToCamelCase);
			mapper.from(getAggressiveLazyLoading()).to(target::setAggressiveLazyLoading);
			mapper.from(getMultipleResultSetsEnabled()).to(target::setMultipleResultSetsEnabled);
			mapper.from(getUseGeneratedKeys()).to(target::setUseGeneratedKeys);
			mapper.from(getUseColumnLabel()).to(target::setUseColumnLabel);
			mapper.from(getCacheEnabled()).to(target::setCacheEnabled);
			mapper.from(getCallSettersOnNulls()).to(target::setCallSettersOnNulls);
			mapper.from(getUseActualParamName()).to(target::setUseActualParamName);
			mapper.from(getReturnInstanceForEmptyRow()).to(target::setReturnInstanceForEmptyRow);
			mapper.from(getShrinkWhitespacesInSql()).to(target::setShrinkWhitespacesInSql);
			mapper.from(getNullableOnForEach()).to(target::setNullableOnForEach);
			mapper.from(getArgNameBasedConstructorAutoMapping()).to(target::setArgNameBasedConstructorAutoMapping);
			mapper.from(getLazyLoadingEnabled()).to(target::setLazyLoadingEnabled);
			mapper.from(getLogPrefix()).to(target::setLogPrefix);
			mapper.from(getLazyLoadTriggerMethods()).to(target::setLazyLoadTriggerMethods);
			mapper.from(getDefaultStatementTimeout()).to(target::setDefaultStatementTimeout);
			mapper.from(getDefaultFetchSize()).to(target::setDefaultFetchSize);
			mapper.from(getLocalCacheScope()).to(target::setLocalCacheScope);
			mapper.from(getJdbcTypeForNull()).to(target::setJdbcTypeForNull);
			mapper.from(getDefaultResultSetType()).to(target::setDefaultResultSetType);
			mapper.from(getDefaultExecutorType()).to(target::setDefaultExecutorType);
			mapper.from(getAutoMappingBehavior()).to(target::setAutoMappingBehavior);
			mapper.from(getAutoMappingUnknownColumnBehavior()).to(target::setAutoMappingUnknownColumnBehavior);
			mapper.from(getVariables()).to(target::setVariables);
			mapper.from(getLogImpl()).to(target::setLogImpl);
			mapper.from(getVfsImpl()).to(target::setVfsImpl);
			mapper.from(getDefaultSqlProviderType()).to(target::setDefaultSqlProviderType);
			mapper.from(getConfigurationFactory()).to(target::setConfigurationFactory);
			mapper.from(getDefaultEnumTypeHandler()).to(target::setDefaultEnumTypeHandler);
			mapper.from(getDatabaseId()).to(target::setDatabaseId);
		}

	}

}
