package com.yuehuanghun.mybatis.milu.example;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisLanguageDriverAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.id.IdentifierGenerator;

@Configuration
@AutoConfigureAfter({ MybatisLanguageDriverAutoConfiguration.class })
public class MybatisMiluConfiguration {
	private final Interceptor[] interceptors;

	@SuppressWarnings("rawtypes")
	private TypeHandler[] typeHandlers = null;

	private final LanguageDriver[] languageDrivers;

	private final ResourceLoader resourceLoader;

	private final DatabaseIdProvider databaseIdProvider;

	private final List<ConfigurationCustomizer> configurationCustomizers;

	@SuppressWarnings("rawtypes")
	public MybatisMiluConfiguration(ObjectProvider<Interceptor[]> interceptorsProvider,
			ObjectProvider<TypeHandler[]> typeHandlersProvider,
			ObjectProvider<LanguageDriver[]> languageDriversProvider, ResourceLoader resourceLoader,
			ObjectProvider<DatabaseIdProvider> databaseIdProvider,
			ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
		this.interceptors = interceptorsProvider.getIfAvailable();
		this.typeHandlers = typeHandlersProvider.getIfAvailable();
		this.languageDrivers = languageDriversProvider.getIfAvailable();
		this.resourceLoader = resourceLoader;
		this.databaseIdProvider = databaseIdProvider.getIfAvailable();
		this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	}

	@Bean
	@ConfigurationProperties(prefix = "datasources.example1.mybatis")
	public MybatisProperties example1MybatisProperties() {
		return new MybatisProperties();
	}

	@Bean
	@ConfigurationProperties(prefix = "datasources.example2.mybatis")
	public MybatisProperties example2MybatisProperties() {
		return new MybatisProperties();
	}

	@Bean
	public SqlSessionFactory example1SqlSessionFactory(@Qualifier("example1Source") DataSource dataSource,
			@Qualifier("example1MybatisProperties") MybatisProperties properties,
			ObjectProvider<List<IdentifierGenerator>> identifierGeneratorsProvider) throws Exception {
		parseConfiguration(properties);
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		if (StringUtils.hasText(properties.getConfigLocation())) {
			factory.setConfigLocation(this.resourceLoader.getResource(properties.getConfigLocation()));
		}
		applyConfiguration(factory, properties, identifierGeneratorsProvider.getIfAvailable());
		if (properties.getConfigurationProperties() != null) {
			factory.setConfigurationProperties(properties.getConfigurationProperties());
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (this.databaseIdProvider != null) {
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
			factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
		}
		if (properties.getTypeAliasesSuperType() != null) {
			factory.setTypeAliasesSuperType(properties.getTypeAliasesSuperType());
		}
		if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
		}
		if (!ObjectUtils.isEmpty(this.typeHandlers)) {
			factory.setTypeHandlers(this.typeHandlers);
		}
		if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
			factory.setMapperLocations(properties.resolveMapperLocations());
		}
		Set<String> factoryPropertyNames = Stream
				.of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors())
				.map(PropertyDescriptor::getName).collect(Collectors.toSet());
		Class<? extends LanguageDriver> defaultLanguageDriver = properties.getDefaultScriptingLanguageDriver();
		if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
			factory.setScriptingLanguageDrivers(this.languageDrivers);
			if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
				defaultLanguageDriver = this.languageDrivers[0].getClass();
			}
		}
		if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
			factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
		}

		return factory.getObject();
	}

	private void applyConfiguration(SqlSessionFactoryBean factory, MybatisProperties properties,
			List<IdentifierGenerator> identifierGeneratorList) {
		MiluConfiguration configuration = properties.getConfiguration();
		if (identifierGeneratorList != null) {
			for (IdentifierGenerator identifierGenerator : identifierGeneratorList) {
				configuration.addIdentifierGenerator(identifierGenerator);
			}
		}
		if (!CollectionUtils.isEmpty(this.configurationCustomizers)) {
			for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
				customizer.customize(configuration);
			}
		}
		if (properties != null) {
			configuration.setIdentifierWrapQuote(properties.isIdentifierWrapQuote());
		}
		factory.setConfiguration(configuration);
	}

	@Bean
	public SqlSessionTemplate example1SqlSessionTemplate(
			@Qualifier("example1SqlSessionFactory") SqlSessionFactory sqlSessionFactory,
			@Qualifier("example1MybatisProperties") MybatisProperties properties) {
		ExecutorType executorType = properties.getExecutorType();
		if (executorType != null) {
			return new SqlSessionTemplate(sqlSessionFactory, executorType);
		} else {
			return new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	@Bean
	public SqlSessionFactory example2SqlSessionFactory(@Qualifier("example2Source") DataSource dataSource,
			@Qualifier("example2MybatisProperties") MybatisProperties properties,
			ObjectProvider<List<IdentifierGenerator>> identifierGeneratorsProvider) throws Exception {
		parseConfiguration(properties);
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		if (StringUtils.hasText(properties.getConfigLocation())) {
			factory.setConfigLocation(this.resourceLoader.getResource(properties.getConfigLocation()));
		}
		applyConfiguration(factory, properties, identifierGeneratorsProvider.getIfAvailable());
		if (properties.getConfigurationProperties() != null) {
			factory.setConfigurationProperties(properties.getConfigurationProperties());
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (this.databaseIdProvider != null) {
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
			factory.setTypeAliasesPackage(properties.getTypeAliasesPackage());
		}
		if (properties.getTypeAliasesSuperType() != null) {
			factory.setTypeAliasesSuperType(properties.getTypeAliasesSuperType());
		}
		if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(properties.getTypeHandlersPackage());
		}
		if (!ObjectUtils.isEmpty(this.typeHandlers)) {
			factory.setTypeHandlers(this.typeHandlers);
		}
		if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
			factory.setMapperLocations(properties.resolveMapperLocations());
		}
		Set<String> factoryPropertyNames = Stream
				.of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors())
				.map(PropertyDescriptor::getName).collect(Collectors.toSet());
		Class<? extends LanguageDriver> defaultLanguageDriver = properties.getDefaultScriptingLanguageDriver();
		if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
			factory.setScriptingLanguageDrivers(this.languageDrivers);
			if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
				defaultLanguageDriver = this.languageDrivers[0].getClass();
			}
		}
		if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
			factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
		}

		return factory.getObject();
	}
	
	@Bean
	public SqlSessionTemplate example2SqlSessionTemplate(@Qualifier("example2SqlSessionFactory") SqlSessionFactory sqlSessionFactory, 
			@Qualifier("example2MybatisProperties") MybatisProperties properties) {
		ExecutorType executorType = properties.getExecutorType();
		if (executorType != null) {
			return new SqlSessionTemplate(sqlSessionFactory, executorType);
		} else {
			return new SqlSessionTemplate(sqlSessionFactory);
		}
	}
	
	private void parseConfiguration(MybatisProperties properties) {
		if(properties.getConfiguration() == null) {
			if(StringUtils.hasText(properties.getConfigLocation())) {
				try {
					Resource configResource = this.resourceLoader.getResource(properties.getConfigLocation());
					XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(configResource.getInputStream(), null, null);
					org.apache.ibatis.session.Configuration targetConfiguration = xmlConfigBuilder.getConfiguration();
					xmlConfigBuilder.parse();
					
					MiluConfiguration configuration = new MiluConfiguration();
					BeanUtils.copyProperties(targetConfiguration, configuration);
					properties.setConfiguration(configuration);
					properties.setConfigLocation(null); //清除
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				properties.setConfiguration(new MiluConfiguration());
			}
		}
	}
}
