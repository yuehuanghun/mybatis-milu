/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuehuanghun.mybatis.milu;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.Case;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.TypeDiscriminator;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.Discriminator;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import com.yuehuanghun.mybatis.milu.annotation.NamingQuery;
import com.yuehuanghun.mybatis.milu.annotation.StatementOptions;
import com.yuehuanghun.mybatis.milu.data.NamedQuerySqlSource;
import com.yuehuanghun.mybatis.milu.data.PartTree;
import com.yuehuanghun.mybatis.milu.data.SqlBuilder;
import com.yuehuanghun.mybatis.milu.exception.SqlExpressionBuildingException;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSql;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderSqlSource;
import com.yuehuanghun.mybatis.milu.id.AssignKeyGenerator;
import com.yuehuanghun.mybatis.milu.id.SequenceKeyGenerator;
import com.yuehuanghun.mybatis.milu.id.SequenceSqlSource;
import com.yuehuanghun.mybatis.milu.id.TableKeyGenerator;
import com.yuehuanghun.mybatis.milu.id.tablekey.TableKeySelectSqlSource;
import com.yuehuanghun.mybatis.milu.id.tablekey.TableKeyUpdateSqlSource;
import com.yuehuanghun.mybatis.milu.mapping.MiluMapperBuilderAssistant;
import com.yuehuanghun.mybatis.milu.metamodel.Entity;
import com.yuehuanghun.mybatis.milu.metamodel.EntityBuilder;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.IdAttribute;
import com.yuehuanghun.mybatis.milu.tool.StringUtils;

public class MapperNamingQueryBuilder {
	private final MiluConfiguration configuration;
	private final MiluMapperBuilderAssistant assistant;
	private final Class<?> type;
	private final static List<ResultFlag> ID_FLAG_LIST = Arrays.asList(ResultFlag.ID);

	public MapperNamingQueryBuilder(Configuration configuration, Class<?> type) {
		String resource = type.getName().replace('.', '/') + ".java (best guess)";
		this.assistant = new MiluMapperBuilderAssistant(configuration, resource);
		this.configuration = (MiluConfiguration) configuration;
		this.type = type;
	}

	public void parse() {
		this.assistant.setCurrentNamespace(type.getName());
		
		Class<?> entityClass = getGenericEntity(type);
		EntityBuilder.instance(entityClass, configuration).build();
		
		for (Method method : type.getDeclaredMethods()) {
			if (!method.isAnnotationPresent(NamingQuery.class)) {
				continue;
			}
			
			if (method.getAnnotation(ResultMap.class) == null) {
				parseResultMap(method);
			}
			parseStatement(method);
		}
		
		if(!BaseMapper.class.isAssignableFrom(type)) {
			return;
		}
		
		for (Method method : BaseMapper.class.getDeclaredMethods()) {
			parseGenericResultMap(method, entityClass);
			
			parseGenericStatement(method, entityClass);
		}
		
	}

	private String parseResultMap(Method method) {
		Class<?> returnType = getReturnType(method);
		Arg[] args = method.getAnnotationsByType(Arg.class);
		Result[] results = method.getAnnotationsByType(Result.class);
		TypeDiscriminator typeDiscriminator = method.getAnnotation(TypeDiscriminator.class);
		String resultMapId = generateResultMapName(method);
		applyResultMap(resultMapId, returnType, args, results, typeDiscriminator, method);
		return resultMapId;
	}

	private String generateResultMapName(Method method) {
		Results results = method.getAnnotation(Results.class);
		if (results != null && !results.id().isEmpty()) {
			return type.getName() + "." + results.id();
		}
		StringBuilder suffix = new StringBuilder();
		for (Class<?> c : method.getParameterTypes()) {
			suffix.append("-");
			suffix.append(c.getSimpleName());
		}
		if (suffix.length() < 1) {
			suffix.append("-void");
		}
		return type.getName() + "." + method.getName() + suffix;
	}

	private void applyResultMap(String resultMapId, Class<?> returnType, Arg[] args, Result[] results,
			TypeDiscriminator discriminator, Method method) {
		List<ResultMapping> resultMappings = new ArrayList<>();
		applyConstructorArgs(args, returnType, resultMappings);
		applyResults(results, returnType, resultMappings);
		Discriminator disc = applyDiscriminator(resultMapId, returnType, discriminator);
		
		if(resultMappings.isEmpty()) {
			if(returnType == getGenericEntity(method.getDeclaringClass())) {
				buildEntityResultMapping(returnType, resultMappings);
			} else {
				buildNotEntityResultMapping(returnType, returnType, resultMappings);
			}
		}
		
		assistant.addResultMap(resultMapId, returnType, null, disc, resultMappings, null);
		
		createDiscriminatorResultMaps(resultMapId, returnType, discriminator);
	}
	
	private void buildEntityResultMapping(Class<?> entityType, List<ResultMapping> resultMappings) {
		for(Attribute attr : configuration.getMetaModel().getEntity(entityType).getAttributes()) {
			if(attr.isSelectable()) {
				ResultMapping resultMapping = assistant.buildResultMapping(entityType, attr.getName(), attr.getColumnName(), attr.getJavaType(), null, null, null, null, null, attr.getTypeHandler(), attr.isId() ? ID_FLAG_LIST : null);
				resultMappings.add(resultMapping);
			}
		}
	}
	
	private void buildNotEntityResultMapping(Class<?> resultType, Class<?> subType, List<ResultMapping> resultMappings) {		
		assistant.buildResultMapping(resultType, subType, resultMappings);
	}

	private void createDiscriminatorResultMaps(String resultMapId, Class<?> resultType,
			TypeDiscriminator discriminator) {
		if (discriminator != null) {
			for (Case c : discriminator.cases()) {
				String caseResultMapId = resultMapId + "-" + c.value();
				List<ResultMapping> resultMappings = new ArrayList<>();
				applyConstructorArgs(c.constructArgs(), resultType, resultMappings);
				applyResults(c.results(), resultType, resultMappings);
				assistant.addResultMap(caseResultMapId, c.type(), resultMapId, null, resultMappings, null);
			}
		}
	}

	private Discriminator applyDiscriminator(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
		if (discriminator != null) {
			String column = discriminator.column();
			Class<?> javaType = discriminator.javaType() == void.class ? String.class : discriminator.javaType();
			JdbcType jdbcType = discriminator.jdbcType() == JdbcType.UNDEFINED ? null : discriminator.jdbcType();
			@SuppressWarnings("unchecked")
			Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>) (discriminator
					.typeHandler() == UnknownTypeHandler.class ? null : discriminator.typeHandler());
			Case[] cases = discriminator.cases();
			Map<String, String> discriminatorMap = new HashMap<>();
			for (Case c : cases) {
				String value = c.value();
				String caseResultMapId = resultMapId + "-" + value;
				discriminatorMap.put(value, caseResultMapId);
			}
			return assistant.buildDiscriminator(resultType, column, javaType, jdbcType, typeHandler, discriminatorMap);
		}
		return null;
	}

	void parseStatement(Method method) {
		final Class<?> parameterTypeClass = getParameterType(method);
		final LanguageDriver languageDriver = configuration.getLanguageDriver(XMLLanguageDriver.class);

		AnnotationWrapper statementAnnotation = getAnnotationWrapper(method, true, NamingQuery.class).get();
		
		final SqlSource sqlSource = buildSqlSource(parameterTypeClass, languageDriver, method);
		final SqlCommandType sqlCommandType = statementAnnotation.getSqlCommandType() == SqlCommandType.UNKNOWN ? getSqlCommandType(method) : statementAnnotation.getSqlCommandType();
		final Options options = getAnnotationWrapper(method, false, Options.class)
				.map(x -> (Options) x.getAnnotation()).orElse(null);
		final String mappedStatementId = type.getName() + "." + method.getName();

		KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
		String keyProperty = null;
		String keyColumn = null;
		if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
			Class<?> entityClass = getGenericEntity(type);
			Entity entity = configuration.getMetaModel().getEntity(entityClass);
			IdAttribute idAttr = entity.getId();
			if(idAttr != null) {
				Field idFiled = idAttr.getField();
				GenerationType generatorType = idAttr.getGenerationType();
				if (generatorType != null) {
					keyProperty = idAttr.getName();
					keyColumn = idAttr.getColumnName();
					if(generatorType == GenerationType.IDENTITY) {
						keyGenerator = Jdbc3KeyGenerator.INSTANCE;
					} else if(generatorType == GenerationType.SEQUENCE){
						SequenceGenerator sequenceGenerator = idFiled.getAnnotation(SequenceGenerator.class);
						if(sequenceGenerator != null) {
							if(configuration.hasKeyGenerator(sequenceGenerator.name())){
								keyGenerator = configuration.getKeyGenerator(sequenceGenerator.name());
							} else {
								keyGenerator = handleSequenceGeneratorAnnotation(sequenceGenerator, mappedStatementId, keyProperty, keyColumn, idAttr.getJavaType(), languageDriver);							
								configuration.addKeyGenerator(sequenceGenerator.name(), keyGenerator);
							}
						}
					} else if(generatorType == GenerationType.TABLE){
						TableGenerator tableGenerator = idFiled.getAnnotation(TableGenerator.class);
						if(tableGenerator != null) {
							if(configuration.hasKeyGenerator(tableGenerator.name())){
								keyGenerator = configuration.getKeyGenerator(tableGenerator.name());
							} else {
								keyGenerator = handleTableGeneratorAnnotation(tableGenerator, mappedStatementId, keyProperty, keyColumn, languageDriver);
								configuration.addKeyGenerator(tableGenerator.name(), keyGenerator);
							}
						}
					} else if(generatorType == GenerationType.AUTO){
						keyGenerator = new AssignKeyGenerator(idAttr.getGenerator(), configuration, entityClass, keyProperty);
					}
				}
			}
		}

		Integer fetchSize = null;
		Integer timeout = null;
		StatementType statementType = StatementType.PREPARED;
		ResultSetType resultSetType = configuration.getDefaultResultSetType();
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
		boolean flushCache = !isSelect;
		boolean useCache = isSelect;
		if (options != null) {
			if (FlushCachePolicy.TRUE.equals(options.flushCache())) {
				flushCache = true;
			} else if (FlushCachePolicy.FALSE.equals(options.flushCache())) {
				flushCache = false;
			}
			useCache = options.useCache();
			fetchSize = options.fetchSize() > -1 || options.fetchSize() == Integer.MIN_VALUE ? options.fetchSize()
					: null;
			timeout = options.timeout() > -1 ? options.timeout() : null;
			statementType = options.statementType();
			if (options.resultSetType() != ResultSetType.DEFAULT) {
				resultSetType = options.resultSetType();
			}
		}

		String resultMapId = null;
		if (isSelect) {
			ResultMap resultMapAnnotation = method.getAnnotation(ResultMap.class);
			if (resultMapAnnotation != null) {
				resultMapId = String.join(",", resultMapAnnotation.value());
			} else {
				resultMapId = generateResultMapName(method);
			}
		}

		assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize,
				timeout,
				null, parameterTypeClass, resultMapId, getReturnType(method), resultSetType, flushCache, useCache,
				false, keyGenerator, keyProperty, keyColumn, statementAnnotation.getDatabaseId(), languageDriver,
				options != null ? empty2Null(options.resultSets()) : null);
	}

	private Class<?> getParameterType(Method method) {
		Class<?> parameterType = null;
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (Class<?> currentParameterType : parameterTypes) {
			if (!RowBounds.class.isAssignableFrom(currentParameterType)
					&& !ResultHandler.class.isAssignableFrom(currentParameterType)) {
				if (parameterType == null) {
					parameterType = currentParameterType;
				} else {
					parameterType = ParamMap.class;
				}
			}
		}
		return parameterType;
	}

	private Class<?> getReturnType(Method method) {
		Class<?> returnType = method.getReturnType();
		Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
		if (resolvedReturnType instanceof Class) {
			returnType = (Class<?>) resolvedReturnType;
			if (returnType.isArray()) {
				returnType = returnType.getComponentType();
			}
			if (void.class.equals(returnType)) {
				ResultType rt = method.getAnnotation(ResultType.class);
				if (rt != null) {
					returnType = rt.value();
				}
			}
		} else if (resolvedReturnType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
			Class<?> rawType = (Class<?>) parameterizedType.getRawType();
			if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					Type returnTypeParameter = actualTypeArguments[0];
					if (returnTypeParameter instanceof Class<?>) {
						returnType = (Class<?>) returnTypeParameter;
					} else if (returnTypeParameter instanceof ParameterizedType) {
						returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
					} else if (returnTypeParameter instanceof GenericArrayType) {
						Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter)
								.getGenericComponentType();
						returnType = Array.newInstance(componentType, 0).getClass();
					}
				}
			} else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 2) {
					Type returnTypeParameter = actualTypeArguments[1];
					if (returnTypeParameter instanceof Class<?>) {
						returnType = (Class<?>) returnTypeParameter;
					} else if (returnTypeParameter instanceof ParameterizedType) {
						returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
					}
				}
			} else if (Optional.class.equals(rawType)) {
				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				Type returnTypeParameter = actualTypeArguments[0];
				if (returnTypeParameter instanceof Class<?>) {
					returnType = (Class<?>) returnTypeParameter;
				}
			}
		}

		return returnType;
	}

	private void applyResults(Result[] results, Class<?> resultType, List<ResultMapping> resultMappings) {
		for (Result result : results) {
			List<ResultFlag> flags = new ArrayList<>();
			if (result.id()) {
				flags.add(ResultFlag.ID);
			}
			@SuppressWarnings("unchecked")
			Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>) ((result
					.typeHandler() == UnknownTypeHandler.class) ? null : result.typeHandler());
			boolean hasNestedResultMap = hasNestedResultMap(result);
			ResultMapping resultMapping = assistant.buildResultMapping(resultType, empty2Null(result.property()),
					empty2Null(result.column()), result.javaType() == void.class ? null : result.javaType(),
					result.jdbcType() == JdbcType.UNDEFINED ? null : result.jdbcType(),
					hasNestedSelect(result) ? nestedSelectId(result) : null,
					hasNestedResultMap ? nestedResultMapId(result) : null, null,
					hasNestedResultMap ? findColumnPrefix(result) : null, typeHandler, flags, null, null,
					isLazy(result));
			resultMappings.add(resultMapping);
		}
	}

	private String findColumnPrefix(Result result) {
		String columnPrefix = result.one().columnPrefix();
		if (columnPrefix.length() < 1) {
			columnPrefix = result.many().columnPrefix();
		}
		return columnPrefix;
	}

	private String nestedResultMapId(Result result) {
		String resultMapId = result.one().resultMap();
		if (resultMapId.length() < 1) {
			resultMapId = result.many().resultMap();
		}
		if (!resultMapId.contains(".")) {
			resultMapId = type.getName() + "." + resultMapId;
		}
		return resultMapId;
	}

	private boolean hasNestedResultMap(Result result) {
		if (result.one().resultMap().length() > 0 && result.many().resultMap().length() > 0) {
			throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
		}
		return result.one().resultMap().length() > 0 || result.many().resultMap().length() > 0;
	}

	private String nestedSelectId(Result result) {
		String nestedSelect = result.one().select();
		if (nestedSelect.length() < 1) {
			nestedSelect = result.many().select();
		}
		if (!nestedSelect.contains(".")) {
			nestedSelect = type.getName() + "." + nestedSelect;
		}
		return nestedSelect;
	}

	private boolean isLazy(Result result) {
		boolean isLazy = configuration.isLazyLoadingEnabled();
		if (result.one().select().length() > 0 && FetchType.DEFAULT != result.one().fetchType()) {
			isLazy = result.one().fetchType() == FetchType.LAZY;
		} else if (result.many().select().length() > 0 && FetchType.DEFAULT != result.many().fetchType()) {
			isLazy = result.many().fetchType() == FetchType.LAZY;
		}
		return isLazy;
	}

	private boolean hasNestedSelect(Result result) {
		if (result.one().select().length() > 0 && result.many().select().length() > 0) {
			throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
		}
		return result.one().select().length() > 0 || result.many().select().length() > 0;
	}

	private void applyConstructorArgs(Arg[] args, Class<?> resultType, List<ResultMapping> resultMappings) {
		for (Arg arg : args) {
			List<ResultFlag> flags = new ArrayList<>();
			flags.add(ResultFlag.CONSTRUCTOR);
			if (arg.id()) {
				flags.add(ResultFlag.ID);
			}
			@SuppressWarnings("unchecked")
			Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>) (arg
					.typeHandler() == UnknownTypeHandler.class ? null : arg.typeHandler());
			ResultMapping resultMapping = assistant.buildResultMapping(resultType, empty2Null(arg.name()),
					empty2Null(arg.column()), arg.javaType() == void.class ? null : arg.javaType(),
					arg.jdbcType() == JdbcType.UNDEFINED ? null : arg.jdbcType(), empty2Null(arg.select()),
					empty2Null(arg.resultMap()), null, empty2Null(arg.columnPrefix()), typeHandler, flags, null, null,
					false);
			resultMappings.add(resultMapping);
		}
	}

	private String empty2Null(String value) {
		return value == null || value.trim().length() == 0 ? null : value;
	}

	private SqlSource buildSqlSource(Class<?> parameterType, LanguageDriver languageDriver,
			Method method) {
		Class<?> entityClass = getGenericEntity(method.getDeclaringClass());
		PartTree tree = new PartTree(getExpression(method), entityClass);
		
		return new NamedQuerySqlSource(configuration, SqlBuilder.instance(entityClass, method, tree, this.configuration).build(), parameterType, configuration.getMetaModel().getEntity(entityClass));
	}

	private String getExpression(Method method) {
		if(method.isAnnotationPresent(StatementOptions.class)) {
			StatementOptions options = method.getAnnotation(StatementOptions.class);
			if(StringUtils.isNotBlank(options.asExpression())) {
				return options.asExpression();
			}
		}
		
		return method.getName();
	}

	@SafeVarargs
	private final Optional<AnnotationWrapper> getAnnotationWrapper(Method method, boolean errorIfNoMatch,
			Class<? extends Annotation>... targetTypes) {
		return getAnnotationWrapper(method, errorIfNoMatch, Arrays.asList(targetTypes));
	}

	private Optional<AnnotationWrapper> getAnnotationWrapper(Method method, boolean errorIfNoMatch,
			Collection<Class<? extends Annotation>> targetTypes) {
		String databaseId = configuration.getDatabaseId();
		Map<String, AnnotationWrapper> statementAnnotations = targetTypes.stream()
				.flatMap(x -> Arrays.stream(method.getAnnotationsByType(x))).map(AnnotationWrapper::new)
				.collect(Collectors.toMap(AnnotationWrapper::getDatabaseId, x -> x, (existing, duplicate) -> {
					throw new BuilderException(String.format("Detected conflicting annotations '%s' and '%s' on '%s'.",
							existing.getAnnotation(), duplicate.getAnnotation(),
							method.getDeclaringClass().getName() + "." + method.getName()));
				}));
		AnnotationWrapper annotationWrapper = null;
		if (databaseId != null) {
			annotationWrapper = statementAnnotations.get(databaseId);
		}
		if (annotationWrapper == null) {
			annotationWrapper = statementAnnotations.get("");
		}
		if (errorIfNoMatch && annotationWrapper == null && !statementAnnotations.isEmpty()) {
			throw new BuilderException(String.format(
					"Could not find a statement annotation that correspond a current database or default statement on method '%s.%s'. Current database id is [%s].",
					method.getDeclaringClass().getName(), method.getName(), databaseId));
		}
		return Optional.ofNullable(annotationWrapper);
	}
	
	private SqlCommandType getSqlCommandType(Method method) {
		String methodName = method.getName();
		if(methodName.matches("^(find|read|get|query|stream|count|sum|count|min|max|avg).*")) {
			return SqlCommandType.SELECT;
		} else if(methodName.matches("^(delete|remove).*")) {
			return SqlCommandType.DELETE;
		}
		
		return SqlCommandType.UNKNOWN;
	}
	
	private Class<?> getGenericEntity(Class<?> mapperClass){
		Type[] interfaces = mapperClass.getGenericInterfaces();
		for(Type interfType : interfaces) {
			if(!(interfType instanceof ParameterizedType)) {
				continue;
			}
			ParameterizedType parameterizedType = (ParameterizedType)interfType;
			Type rawType = parameterizedType.getRawType();
			if(rawType.equals(BaseMapper.class)) {
				return (Class<?>) parameterizedType.getActualTypeArguments()[0];
			}
		}
		throw new SqlExpressionBuildingException("Mapper非BaseMapper子类");
	}
	
	private String parseGenericResultMap(Method method, Class<?> entityClass) {
		Class<?> returnType;
		List<ResultMapping> resultMappings = new ArrayList<>();
		
		if(method.getName().startsWith("find")) {
			returnType = entityClass;
			buildEntityResultMapping(returnType, resultMappings);
		} else if(method.getName().startsWith("statistic")) {
			returnType = Map.class;
		} else {
			returnType = int.class;
		}
		String resultMapId = generateResultMapName(method);

		assistant.addResultMap(resultMapId, returnType, null, null, resultMappings, null);
		return resultMapId;
	}

	void parseGenericStatement(Method method, Class<?> entityClass) {
		String methodName = method.getName();
		final String mappedStatementId = type.getName() + "." + methodName;
		if(configuration.hasStatement(mappedStatementId)) { //允许同名statement，Lambda模式与非Lambda模式
			return;
		}
		GenericProviderSql genericProviderSql = this.configuration.getGenericProviderSql(methodName);
		
		SqlCommandType sqlCommandType = SqlCommandType.UNKNOWN;
		if(methodName.startsWith("find") || methodName.startsWith("count") || methodName.startsWith("statistic")) {
			sqlCommandType = SqlCommandType.SELECT;
		} else if(methodName.startsWith("delete")) {
			sqlCommandType = SqlCommandType.DELETE;
		} else if(methodName.equals("insert") || methodName.equals("batchInsert")) {
			sqlCommandType = SqlCommandType.INSERT;
		} else if(methodName.startsWith("update") || methodName.startsWith("logic") || methodName.startsWith("resume")) {
			sqlCommandType = SqlCommandType.UPDATE;
		} else if(methodName.startsWith("flush")) {
			sqlCommandType = SqlCommandType.FLUSH;
		}
	
		final Class<?> parameterTypeClass = getParameterType(method);
		final LanguageDriver languageDriver = configuration.getLanguageDriver(XMLLanguageDriver.class);
		
		KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
		String keyProperty = null;
		String keyColumn = null;
		Entity entity = configuration.getMetaModel().getEntity(entityClass);
		
		if(sqlCommandType == SqlCommandType.INSERT) {
			IdAttribute idAttr = entity.getId();
			if(idAttr != null) {
				keyProperty = idAttr.getName();
				keyColumn = idAttr.getColumnName();
				keyGenerator = Jdbc3KeyGenerator.INSTANCE;
				Field idFiled = idAttr.getField();
				GenerationType generationType = idAttr.getGenerationType();
				if (generationType != null) {
					keyProperty = idAttr.getName();
					keyColumn = idAttr.getColumnName();
					if(generationType == GenerationType.IDENTITY) {
						keyGenerator = Jdbc3KeyGenerator.INSTANCE;
					} else if(generationType == GenerationType.SEQUENCE){
						SequenceGenerator sequenceGenerator = idFiled.getAnnotation(SequenceGenerator.class);
						if(sequenceGenerator != null) {
							if(configuration.hasKeyGenerator(sequenceGenerator.name())){
								keyGenerator = configuration.getKeyGenerator(sequenceGenerator.name());
							} else {
								keyGenerator = handleSequenceGeneratorAnnotation(sequenceGenerator, mappedStatementId, keyProperty, keyColumn, idAttr.getJavaType(), languageDriver);							
								configuration.addKeyGenerator(sequenceGenerator.name(), keyGenerator);
							}
						}
					} else if(generationType== GenerationType.TABLE){
						TableGenerator tableGenerator = idFiled.getAnnotation(TableGenerator.class);
						if(tableGenerator != null) {
							if(configuration.hasKeyGenerator(tableGenerator.name())){
								keyGenerator = configuration.getKeyGenerator(tableGenerator.name());
							} else {
								keyGenerator = handleTableGeneratorAnnotation(tableGenerator, mappedStatementId, keyProperty, keyColumn, languageDriver);
								configuration.addKeyGenerator(tableGenerator.name(), keyGenerator);
							}
						}
					} else if(generationType == GenerationType.AUTO){
						keyGenerator = new AssignKeyGenerator(StringUtils.defaultIfBlank(idAttr.getGenerator(), configuration.getDefaultIdGenerator()), configuration, entityClass, keyProperty);
					}
				}
			}
		}
		
		Integer fetchSize = null;
		Integer timeout = null;
		StatementType statementType = StatementType.PREPARED;
		ResultSetType resultSetType = configuration.getDefaultResultSetType();
		
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
		boolean flushCache = !isSelect;
		boolean useCache = isSelect;
		
		String resultMapId = generateResultMapName(method);
		
		SqlSource sqlSource = new GenericProviderSqlSource(configuration, genericProviderSql, type, method, keyGenerator, entity);
		
		if("batchInsert".equals(methodName) && Jdbc3KeyGenerator.INSTANCE != keyGenerator) { //特殊处理
			keyGenerator = NoKeyGenerator.INSTANCE;
		}
		
		assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize,
				timeout,
				null, parameterTypeClass, resultMapId, getReturnType(method), resultSetType, flushCache, useCache,
				false, keyGenerator, keyProperty, keyColumn, configuration.getDatabaseId(), languageDriver,null, true);
	}
	
	private KeyGenerator handleSequenceGeneratorAnnotation(SequenceGenerator sequenceGenerator, String baseStatementId, String keyProperty, String keyColumn, Class<?> keyJavaType, LanguageDriver languageDriver) {
	    String id = baseStatementId + "!" + sequenceGenerator.name();
	    // defaults
	    Class<?> resultTypeClass = keyJavaType;
	    Class<?> parameterTypeClass = Object.class;
	    StatementType statementType = StatementType.PREPARED;
	    boolean useCache = false;
	    KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
	    Integer fetchSize = null;
	    Integer timeout = null;
	    boolean flushCache = false;
	    String parameterMap = null;
	    String resultMap = null;
	    ResultSetType resultSetTypeEnum = null;
	    String databaseId = null;

	    SqlSource sqlSource = new SequenceSqlSource(configuration, sequenceGenerator);
	    SqlCommandType sqlCommandType = SqlCommandType.SELECT;

	    assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum,
	        flushCache, useCache, false,
	        keyGenerator, keyProperty, keyColumn, databaseId, languageDriver, null);

	    id = assistant.applyCurrentNamespace(id, false);

	    MappedStatement keyStatement = configuration.getMappedStatement(id, false);
	    SequenceKeyGenerator answer = new SequenceKeyGenerator(keyStatement);
	    configuration.addKeyGenerator(id, answer);
	    return answer;
	}
	
	private KeyGenerator handleTableGeneratorAnnotation(TableGenerator tableGenerator, String baseStatementId, String keyProperty, String keyColumn, LanguageDriver languageDriver) {
	    String baseId = baseStatementId + "!" + tableGenerator.name();

	    // defaults
	    Class<?> parameterTypeClass = Object.class;
	    StatementType statementType = StatementType.PREPARED;
	    boolean useCache = false;
	    KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
	    Integer fetchSize = null;
	    Integer timeout = null;
	    boolean flushCache = false;
	    String parameterMap = null;
	    String resultMap = null;
	    ResultSetType resultSetTypeEnum = null;
	    String databaseId = null;

	    SqlSource selectSqlSource = new TableKeySelectSqlSource(configuration, tableGenerator);
	    SqlSource updateSqlSource = new TableKeyUpdateSqlSource(configuration, tableGenerator);
	    
	    String selectId = baseId + "!select";
	    String updateId = baseId + "!update";
	    

	    assistant.addMappedStatement(selectId, selectSqlSource, statementType, SqlCommandType.SELECT, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, Long.class, resultSetTypeEnum,
	        flushCache, useCache, false,
	        keyGenerator, keyProperty, keyColumn, databaseId, languageDriver, null);

	    selectId = assistant.applyCurrentNamespace(selectId, false);

	    MappedStatement selectKeyStatement = configuration.getMappedStatement(selectId, false);
	    
	    assistant.addMappedStatement(updateId, updateSqlSource, statementType, SqlCommandType.UPDATE, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, void.class, resultSetTypeEnum,
		        flushCache, useCache, false,
		        keyGenerator, keyProperty, keyColumn, databaseId, languageDriver, null);

	    updateId = assistant.applyCurrentNamespace(updateId, false);

		MappedStatement updateKeyStatement = configuration.getMappedStatement(updateId, false);
	    
	    TableKeyGenerator answer = new TableKeyGenerator(selectKeyStatement, updateKeyStatement, tableGenerator.allocationSize());
	    configuration.addKeyGenerator(baseId, answer);
	    return answer;
	}

	private class AnnotationWrapper {
		private final Annotation annotation;
		private final String databaseId;
		private final SqlCommandType sqlCommandType = SqlCommandType.UNKNOWN; //暂时保留

		AnnotationWrapper(Annotation annotation) {
			super();
			this.annotation = annotation;
			if (annotation instanceof NamingQuery) {
				databaseId = ((NamingQuery) annotation).databaseId();
			} else {
				if (annotation instanceof Options) {
					databaseId = ((Options) annotation).databaseId();
				} else if (annotation instanceof SelectKey) {
					databaseId = ((SelectKey) annotation).databaseId();
				} else {
					databaseId = "";
				}
			}
		}

		Annotation getAnnotation() {
			return annotation;
		}

		SqlCommandType getSqlCommandType() {
			return sqlCommandType;
		}

		String getDatabaseId() {
			return databaseId;
		}
	}
}