/*
 * Copyright 2020-current the original author or authors.
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
package com.yuehuanghun.mybatis.milu.criteria;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import com.yuehuanghun.mybatis.milu.data.Part.Type;
import com.yuehuanghun.mybatis.milu.data.SqlBuildingHelper;
import com.yuehuanghun.mybatis.milu.generic.GenericProviderContext;
import com.yuehuanghun.mybatis.milu.metamodel.Entity.Attribute;
import com.yuehuanghun.mybatis.milu.tool.Constants;
import com.yuehuanghun.mybatis.milu.tool.Segment;
import com.yuehuanghun.mybatis.milu.tool.converter.Converter;
import com.yuehuanghun.mybatis.milu.tool.converter.ConverterUtils;

import lombok.Getter;

/**
 * @see Conditions
 *
 */
public class ConditionImpl implements Condition {
	private static Log logger = LogFactory.getLog(ConditionImpl.class);

	@Getter
	private Type type;

	@Getter
	private Object[] params;

	@Getter
	private String attributeName;

	protected ConditionImpl(Type type, String attributeName, Object... params) {
		this.type = type;
		this.attributeName = attributeName;
		this.params = params;
	}

	@Override
	public int renderSqlTemplate(GenericProviderContext context, StringBuilder expressionBuilder, Set<String> columns,
			int paramIndex) {
		Attribute attribute = SqlBuildingHelper.getAttribute(context.getConfiguration(), context.getEntity(),
				getAttributeName(), true);

		String expression = context.getConfiguration().getDialect().getPartTypeExpression(getType());

		Object[] keys = new Object[getParams().length];
		if (getType().getNumberOfArguments() > 0) {
			for (int i = 0; i < getParams().length; i++) {
				String key = attributeName + "_" + paramIndex;
				if (getType() == Type.IN || getType() == Type.NOT_IN) { // collection
					keys[i] = key;
					expression = attribute.formatParameterExpression(expression);
				} else {
					keys[i] = Segment.HASH_LEFT_BRACE + attribute.toParameter(key) + Segment.RIGHT_BRACE;
				}

				paramIndex++;
			}
		}

		String partTypeExpression = String.format(expression, keys);
		if (partTypeExpression.contains(Constants.COLUMN_HOLDER)) {
			expressionBuilder.append(partTypeExpression.replace(Constants.COLUMN_HOLDER,
					Segment.DOLLAR + attributeName + Segment.DOLLAR));
		} else {
			expressionBuilder.append(Segment.DOLLAR).append(attributeName).append(Segment.DOLLAR)
					.append(partTypeExpression);
		}

		columns.add(attributeName);
		return paramIndex;
	}

	@Override
	public int renderParams(GenericProviderContext context, Map<String, Object> params, int paramIndex) {
		if (getType().getNumberOfArguments() > 0) {
			Attribute attribute = null;
			boolean convert = false;
			if (context.getConfiguration().isAutoConvertQueryParamValue() && type != Type.CONTAINING
					&& type != Type.STARTING_WITH && type != Type.ENDING_WITH && type != Type.LIKE
					&& type != Type.NOT_CONTAINING && type != Type.NOT_LIKE) {
				try {
					attribute = SqlBuildingHelper.getAttribute(context.getConfiguration(), context.getEntity(),
							getAttributeName(), true);
					convert = true;
				} catch (Exception e) {
					logger.error("此异常已被捕捉，不影响正常查询，如出现请提issue或联系作者", e);
				}
			}

			for (int i = 0; i < getParams().length; i++) {
				String key = attributeName + "_" + paramIndex;
				Object param = getParams()[i];
				if (convert && param != null && attribute.getJavaType() != param.getClass()) {
					Optional<Converter<?>> converterOpt = ConverterUtils.ofConverter(attribute.getJavaType());
					if (converterOpt.isPresent()) {
						Converter<?> converter = converterOpt.get();
						try {
							if (param instanceof Collection) {
								param = ((Collection<?>) param).stream().map(el -> converter.convert(el))
										.collect(Collectors.toList());
							} else if (param.getClass().isArray()) {
								int length = Array.getLength(param);
								Object[] objs = new Object[length];
								for (int j = 0; j < length; j++) {
									objs[j] = converter.convert(Array.get(param, j));
								}
								param = objs;
							} else {
								param = converter.convert(param);
							}
						} catch (Exception e) {
							logger.error(String.format("无法转换查询条件中的条件值%s的类型，由%s类型转换为%s类型", param,
									param.getClass().getName(), attribute.getJavaType().getName()), e);
						}
					}
				}

				params.put(key, param);
				paramIndex++;
			}
		}

		return paramIndex;
	}

	@Override
	public boolean equals(Object that) {
		if (!this.getClass().isInstance(that)) {
			return false;
		}
		return Objects.equals(this.getType(), ((ConditionImpl) that).getType())
				&& Objects.equals(this.getAttributeName(), ((ConditionImpl) that).getAttributeName());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 31 * result + type.hashCode();
		result = 31 * result + attributeName.hashCode();
		return result;
	}
}
