/**
 *    Copyright 2015-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.spring.boot.autoconfigure;

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.MiluConfiguration;
import com.yuehuanghun.mybatis.milu.id.IdentifierGenerator;
import com.yuehuanghun.mybatis.milu.metamodel.EntityBuilder;
import com.yuehuanghun.mybatis.milu.spring.MapperScanner;
import com.yuehuanghun.mybatis.milu.tool.ConfigPropertyKeys;

/**
 * {@link EnableAutoConfiguration Auto-Configuration} for Mybatis. Contributes a {@link SqlSessionFactory} and a
 * {@link SqlSessionTemplate}.
 *
 * If {@link org.mybatis.spring.annotation.MapperScan} is used, or a configuration file is specified as a property,
 * those will be considered, otherwise this auto-configuration will attempt to register mappers based on the interface
 * definitions in or under the root auto-configuration package.
 *
 * @author Edd?? Mel??ndez
 * @author Josh Long
 * @author Kazuki Shimizu
 * @author Eduardo Macarr??n
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@ConditionalOnSingleCandidate(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter({ DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class })
public class MybatisAutoConfiguration implements InitializingBean, ApplicationContextAware {

  private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

  private final MybatisProperties properties;

  private final Interceptor[] interceptors;

  private final TypeHandler[] typeHandlers;

  private final LanguageDriver[] languageDrivers;

  private final ResourceLoader resourceLoader;

  private final DatabaseIdProvider databaseIdProvider;

  private final List<ConfigurationCustomizer> configurationCustomizers;
  
  private ApplicationContext applicationContext;

  public MybatisAutoConfiguration(MybatisProperties properties, ObjectProvider<Interceptor[]> interceptorsProvider,
      ObjectProvider<TypeHandler[]> typeHandlersProvider, ObjectProvider<LanguageDriver[]> languageDriversProvider,
      ResourceLoader resourceLoader, ObjectProvider<DatabaseIdProvider> databaseIdProvider,
      ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
    this.properties = properties;
    this.interceptors = interceptorsProvider.getIfAvailable();
    this.typeHandlers = typeHandlersProvider.getIfAvailable();
    this.languageDrivers = languageDriversProvider.getIfAvailable();
    this.resourceLoader = resourceLoader;
    this.databaseIdProvider = databaseIdProvider.getIfAvailable();
    this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
  }

  @Override
  public void afterPropertiesSet() {
    checkConfigFileExists();
    parseConfiguration();
    buildEntityResultMap();
  }

  private void checkConfigFileExists() {
    if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
      Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
      Assert.state(resource.exists(),
          "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
    }
  }
  
  private void buildEntityResultMap() {
	  MapperScanner scanner = new MapperScanner(applicationContext);
	  
	  try {
			Set<Class<?>> mapperClassSet = scanner.scan(Mapper.class);
			
			MiluConfiguration configuration = this.properties.getConfiguration();
			
			for(Class<?> mapperClass : mapperClassSet) {
	    		if(!BaseMapper.class.isAssignableFrom(mapperClass)) {
	    			continue;
	    		}
	    		Class<?> entityClass = getGenericEntity(mapperClass);
	    		EntityBuilder.instance(entityClass, (MiluConfiguration)configuration).buildEntityDefaultResultMap(mapperClass); //??????????????????sqlSessionFactory??????????????????????????????????????????ResultMap???sqlSessionFactory?????????????????????MapperXML
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
	
	//????????????????????????????????????????????????
	private void parseConfiguration() {
		if(this.properties.getConfiguration() == null) {
			if(StringUtils.hasText(this.properties.getConfigLocation())) {
				try {
					Resource configResource = this.resourceLoader.getResource(this.properties.getConfigLocation());
					XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(configResource.getInputStream(), null, null);
					Configuration targetConfiguration = xmlConfigBuilder.getConfiguration();
					xmlConfigBuilder.parse();
					
					MiluConfiguration configuration = new MiluConfiguration();
					BeanUtils.copyProperties(targetConfiguration, configuration);
					this.properties.setConfiguration(configuration);
					this.properties.setConfigLocation(null); //??????
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				this.properties.setConfiguration(new MiluConfiguration());
			}
		}
	}

  @Bean
  @ConditionalOnMissingBean
  public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ObjectProvider<List<IdentifierGenerator>> identifierGeneratorsProvider) throws Exception {
    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
    factory.setDataSource(dataSource);
    factory.setVfs(SpringBootVFS.class);
    if (StringUtils.hasText(this.properties.getConfigLocation())) {
      factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
    }
    applyConfiguration(factory, identifierGeneratorsProvider.getIfAvailable());
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
    if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
      factory.setMapperLocations(this.properties.resolveMapperLocations());
    }
    Set<String> factoryPropertyNames = Stream
        .of(new BeanWrapperImpl(SqlSessionFactoryBean.class).getPropertyDescriptors()).map(PropertyDescriptor::getName)
        .collect(Collectors.toSet());
    Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
    if (factoryPropertyNames.contains("scriptingLanguageDrivers") && !ObjectUtils.isEmpty(this.languageDrivers)) {
      // Need to mybatis-spring 2.0.2+
      factory.setScriptingLanguageDrivers(this.languageDrivers);
      if (defaultLanguageDriver == null && this.languageDrivers.length == 1) {
        defaultLanguageDriver = this.languageDrivers[0].getClass();
      }
    }
    if (factoryPropertyNames.contains("defaultScriptingLanguageDriver")) {
      // Need to mybatis-spring 2.0.2+
      factory.setDefaultScriptingLanguageDriver(defaultLanguageDriver);
    }

    return factory.getObject();
  }

	private void applyConfiguration(SqlSessionFactoryBean factory, List<IdentifierGenerator> identifierGeneratorList) {
		MiluConfiguration configuration = this.properties.getConfiguration();
		if (identifierGeneratorList != null) {
			for (IdentifierGenerator identifierGenerator : identifierGeneratorList) {
				configuration.addIdentifierGenerator(identifierGenerator);
			}
		}
		
		if(properties.getConfigurationProperties() != null) {
			properties.getConfiguration().setDefaultIdGenerator(properties.getConfigurationProperties().getProperty(ConfigPropertyKeys.ID_GENERATOR));
			properties.getConfiguration().setDefaultLogicDeleteProvider(properties.getConfigurationProperties().getProperty(ConfigPropertyKeys.LOGIC_DELETE_PROVIDER));
			properties.getConfiguration().setDefaultExampleQueryConverter(properties.getConfigurationProperties().getProperty(ConfigPropertyKeys.EXAMPLE_QUERY_CONVERTER));
		}

		if (!CollectionUtils.isEmpty(this.configurationCustomizers)) {
			for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
				customizer.customize(configuration);
			}
		}
		configuration.setIdentifierWrapQuote(properties.isIdentifierWrapQuote());
		factory.setConfiguration(configuration);
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
  

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

  /**
   * This will just scan the same base package as Spring Boot does. If you want more power, you can explicitly use
   * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed mappers working correctly, out-of-the-box,
   * similar to using Spring Data JPA repositories.
   */
  public static class AutoConfiguredMapperScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {

    private BeanFactory beanFactory;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

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
        // Need to mybatis-spring 2.0.2+
        builder.addPropertyValue("lazyInitialization", "${mybatis.lazy-initialization:false}");
      }
      if (propertyNames.contains("defaultScope")) {
        // Need to mybatis-spring 2.0.6+
        builder.addPropertyValue("defaultScope", "${mybatis.mapper-default-scope:}");
      }
      registry.registerBeanDefinition(MapperScannerConfigurer.class.getName(), builder.getBeanDefinition());
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
      this.beanFactory = beanFactory;
    }

  }

  /**
   * If mapper registering configuration or mapper scanning configuration not present, this configuration allow to scan
   * mappers based on the same component-scanning path as Spring Boot itself.
   */
  @org.springframework.context.annotation.Configuration
  @Import(AutoConfiguredMapperScannerRegistrar.class)
  @ConditionalOnMissingBean({ MapperFactoryBean.class, MapperScannerConfigurer.class })
  public static class MapperScannerRegistrarNotFoundConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
      logger.debug(
          "Not found configuration for registering mapper bean using @MapperScan, MapperFactoryBean and MapperScannerConfigurer.");
    }

  }

}
