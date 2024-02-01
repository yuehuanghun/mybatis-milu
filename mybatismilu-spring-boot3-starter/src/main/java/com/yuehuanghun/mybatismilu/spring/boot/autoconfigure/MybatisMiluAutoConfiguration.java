package com.yuehuanghun.mybatismilu.spring.boot.autoconfigure;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.PlaceholderResolver;
import com.yuehuanghun.mybatis.milu.id.IdentifierGenerator;
import com.yuehuanghun.mybatis.milu.metamodel.EntityBuilder;
import com.yuehuanghun.mybatis.milu.spring.MapperScanner;
import com.yuehuanghun.mybatis.milu.spring.SpringPlaceholderResolver;
import com.yuehuanghun.mybatis.milu.tool.ConfigPropertyKeys;

/**
 * 自动配置类
 */
@org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class })
public class MybatisMiluAutoConfiguration implements InitializingBean, ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(MybatisMiluAutoConfiguration.class);

	private final MybatisProperties properties;

	private final Interceptor[] interceptors;

	private final TypeHandler[] typeHandlers;

	private final LanguageDriver[] languageDrivers;

	private final ResourceLoader resourceLoader;

	private final DatabaseIdProvider databaseIdProvider;

	private final List<ConfigurationCustomizer> configurationCustomizers;

	private final List<SqlSessionFactoryBeanCustomizer> sqlSessionFactoryBeanCustomizers;

	private ApplicationContext applicationContext;

	public MybatisMiluAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
			ObjectProvider<TypeHandler[]> typeHandlersProvider,
			ObjectProvider<LanguageDriver[]> languageDriversProvider, ResourceLoader resourceLoader,
			ObjectProvider<DatabaseIdProvider> databaseIdProvider,
			ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
			ObjectProvider<List<SqlSessionFactoryBeanCustomizer>> sqlSessionFactoryBeanCustomizers,
			ObjectProvider<PlaceholderResolver> placeholderResolver) {
		this.properties = properties;
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.typeHandlers = typeHandlersProvider.getIfAvailable();
		this.languageDrivers = languageDriversProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.databaseIdProvider = databaseIdProvider.getIfAvailable();
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
		this.sqlSessionFactoryBeanCustomizers = sqlSessionFactoryBeanCustomizers.getIfAvailable();
	}

	@Override
	public void afterPropertiesSet() {
		checkConfigFileExists();
	}

	private void checkConfigFileExists() {
		if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
			Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
			Assert.state(resource.exists(), "Cannot find config location: " + resource
					+ " (please add config file or check your Mybatis configuration)");
		}
	}

	private void buildEntityResultMap(MiluConfiguration configuration) {
		MapperScanner scanner = new MapperScanner(applicationContext);

		try {
			Set<Class<?>> mapperClassSet = scanner.scan();

			for (Class<?> mapperClass : mapperClassSet) {
				if (!BaseMapper.class.isAssignableFrom(mapperClass)) {
					continue;
				}
				Class<?> entityClass = getGenericEntity(mapperClass);
				EntityBuilder.instance(entityClass, (MiluConfiguration) configuration)
						.buildEntityDefaultResultMap(mapperClass); // 创建实体要比sqlSessionFactory实例化要早，以提供实体默认的ResultMap，sqlSessionFactory实例化时会解析MapperXML
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private Class<?> getGenericEntity(Class<?> mapperClass) {
		Type[] interfaces = mapperClass.getGenericInterfaces();
		for (Type type : interfaces) {
			if (!(type instanceof ParameterizedType)) {
				continue;
			}
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			if (rawType.equals(BaseMapper.class)) {
				return (Class<?>) parameterizedType.getActualTypeArguments()[0];
			}
		}
		return null;
	}

	// 为了初始化更多数据，必须提前处理
	private MiluConfiguration parseConfiguration() {
		MiluConfiguration configuration = new MiluConfiguration();
		if (StringUtils.hasText(this.properties.getConfigLocation())) {
			try {
				Resource configResource = this.resourceLoader.getResource(this.properties.getConfigLocation());
				XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(configResource.getInputStream(), null, null);
				Configuration targetConfiguration = xmlConfigBuilder.getConfiguration();
				xmlConfigBuilder.parse();

				BeanUtils.copyProperties(targetConfiguration, configuration);
				this.properties.setConfigLocation(null); // 清除
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			MybatisProperties.CoreConfiguration coreConfiguration = this.properties.getConfiguration();
			if(coreConfiguration != null) {
				coreConfiguration.applyTo(configuration);
			}
		}
		if (properties.getConfigurationProperties() != null) {
			configuration.setDefaultIdGenerator(
					properties.getConfigurationProperties().getProperty(ConfigPropertyKeys.ID_GENERATOR));
			configuration.setDefaultLogicDeleteProvider(
					properties.getConfigurationProperties().getProperty(ConfigPropertyKeys.LOGIC_DELETE_PROVIDER));
			configuration.setDefaultExampleQueryConverter(
					properties.getConfigurationProperties().getProperty(ConfigPropertyKeys.EXAMPLE_QUERY_CONVERTER));
		}
		configuration.setIdentifierWrapQuote(properties.isIdentifierWrapQuote());
		configuration.setCreateEntityResultMap(properties.isCreateEntityResultMap());
		configuration.setAutoSetupColumnJdbcType(properties.isAutoSetupColumnJdbcType());
		return configuration;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PlaceholderResolver placeholderResolver() {
		return new SpringPlaceholderResolver();
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource,
			ObjectProvider<List<IdentifierGenerator>> identifierGeneratorsProvider,
			ObjectProvider<PlaceholderResolver> placeholderResolver) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		if (properties.getConfiguration() == null || properties.getConfiguration().getVfsImpl() == null) {
			factory.setVfs(SpringBootVFS.class);
		}
		if (StringUtils.hasText(this.properties.getConfigLocation())) {
			factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
		}
		MiluConfiguration configuration = applyConfiguration(factory);
		buildEntityResultMap(configuration);
		List<IdentifierGenerator> identifierGeneratorList = identifierGeneratorsProvider.getIfAvailable();
		if (identifierGeneratorList != null) {
			for (IdentifierGenerator identifierGenerator : identifierGeneratorList) {
				configuration.addIdentifierGenerator(identifierGenerator);
			}
		}
		configuration.setPlaceholderResolver(placeholderResolver.getIfAvailable());
		if (this.properties.getConfigurationProperties() != null) {
			factory.setConfigurationProperties(this.properties.getConfigurationProperties());
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (this.databaseIdProvider != null) {
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
			factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
		}
		if (this.properties.getTypeAliasesSuperType() != null) {
			factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
		}
		if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
		}
		if (!ObjectUtils.isEmpty(this.typeHandlers)) {
			factory.setTypeHandlers(this.typeHandlers);
		}
		Resource[] mapperLocations = this.properties.resolveMapperLocations();
		if (!ObjectUtils.isEmpty(mapperLocations)) {
			factory.setMapperLocations(mapperLocations);
		}
		Set<String> factoryPropertyNames = Stream
				.of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors())
				.map(PropertyDescriptor::getName).collect(Collectors.toSet());
		Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
		if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
			factory.setScriptingLanguageDrivers(this.languageDrivers);
			if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
				defaultLanguageDriver = this.languageDrivers[0].getClass();
			}
		}
		if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
			factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
		}
		applySqlSessionFactoryBeanCustomizers(factory);
		return factory.getObject();
	}

	private MiluConfiguration applyConfiguration(SqlSessionFactoryBean factory) {
		MiluConfiguration configuration = parseConfiguration();
		if (!CollectionUtils.isEmpty(this.configurationCustomizers)) {
			for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
				customizer.customize(configuration);
			}
		}
		factory.setConfiguration(configuration);
		return configuration;
	}

	private void applySqlSessionFactoryBeanCustomizers(SqlSessionFactoryBean factory) {
		if (!CollectionUtils.isEmpty(this.sqlSessionFactoryBeanCustomizers)) {
			for (SqlSessionFactoryBeanCustomizer customizer : this.sqlSessionFactoryBeanCustomizers) {
				customizer.customize(factory);
			}
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		ExecutorType executorType = this.properties.getExecutorType();
		if (executorType != null) {
			return new SqlSessionTemplate(sqlSessionFactory, executorType);
		} else {
			return new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	/**
	 * Mapper类自动扫描并注册 {@link org.mybatis.spring.annotation.MapperScan}
	 */
	public static class AutoConfiguredMapperScannerRegistrar
			implements BeanFactoryAware, EnvironmentAware, ImportBeanDefinitionRegistrar {

		private BeanFactory beanFactory;
		private Environment environment;

		@Override
		public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
				BeanDefinitionRegistry registry) {

			if (!AutoConfigurationPackages.has(this.beanFactory)) {
				logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.");
				return;
			}

			logger.debug("Searching for mappers annotated with @Mapper");

			List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
			if (logger.isDebugEnabled()) {
				packages.forEach(pkg -> logger.debug("Using auto-configuration base package '{}'", pkg));
			}

			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
			builder.addPropertyValue("processPropertyPlaceHolders", true);
			builder.addPropertyValue("annotationClass", Mapper.class);
			builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));
			BeanWrapper beanWrapper = new BeanWrapperImpl(MapperScannerConfigurer.class);
			Set<String> propertyNames = Stream.of(beanWrapper.getPropertyDescriptors()).map(PropertyDescriptor::getName)
					.collect(Collectors.toSet());
			if (propertyNames.contains("lazyInitialization")) {
				builder.addPropertyValue("lazyInitialization", "${mybatis.lazy-initialization:false}");
			}
			if (propertyNames.contains("defaultScope")) {
				builder.addPropertyValue("defaultScope", "${mybatis.mapper-default-scope:}");
			}

			boolean injectSqlSession = environment.getProperty("mybatis.inject-sql-session-on-mapper-scan",
					Boolean.class, Boolean.TRUE);
			if (injectSqlSession && this.beanFactory instanceof ListableBeanFactory) {
				ListableBeanFactory listableBeanFactory = (ListableBeanFactory) this.beanFactory;
				Optional<String> sqlSessionTemplateBeanName = Optional
						.ofNullable(getBeanNameForType(SqlSessionTemplate.class, listableBeanFactory));
				Optional<String> sqlSessionFactoryBeanName = Optional
						.ofNullable(getBeanNameForType(SqlSessionFactory.class, listableBeanFactory));
				if (sqlSessionTemplateBeanName.isPresent() || !sqlSessionFactoryBeanName.isPresent()) {
					builder.addPropertyValue("sqlSessionTemplateBeanName",
							sqlSessionTemplateBeanName.orElse("sqlSessionTemplate"));
				} else {
					builder.addPropertyValue("sqlSessionFactoryBeanName", sqlSessionFactoryBeanName.get());
				}
			}
			builder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

			registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
		}

		@Override
		public void setBeanFactory(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		@Override
		public void setEnvironment(Environment environment) {
			this.environment = environment;
		}

		private String getBeanNameForType(Class<?> type, ListableBeanFactory factory) {
			String[] beanNames = factory.getBeanNamesForType(type);
			return beanNames.length > 0 ? beanNames[0] : null;
		}

	}

	/**
	 * 如果Mapper注册配置或Mapper扫描配置不存在，则此配置使用基于与Spring Boot本身相同的组件扫描路径进行扫描Mapper。
	 */
	@org.springframework.context.annotation.Configuration(proxyBeanMethods = false)
	@Import(AutoConfiguredMapperScannerRegistrar.class)
	@ConditionalOnMissingBean({ MapperFactoryBean.class, MapperScannerConfigurer.class })
	public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

		@Override
		public void afterPropertiesSet() {
			logger.debug(
					"Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
