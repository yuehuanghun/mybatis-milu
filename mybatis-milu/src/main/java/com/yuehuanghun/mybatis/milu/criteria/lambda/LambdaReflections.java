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
package com.yuehuanghun.mybatis.milu.criteria.lambda;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

public class LambdaReflections {

	private LambdaReflections() {

	}

	public static <T, R> String fnToFieldName(SerializableFunction<T, R> function) {
		try {
			Method method = function.getClass().getDeclaredMethod("writeReplace");
			method.setAccessible(Boolean.TRUE);
			SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
			String getter = serializedLambda.getImplMethodName();
			String fieldName;
			if(getter.startsWith("get")) {
				fieldName = getter.substring(3);
			} else if(getter.startsWith("is")) {
				fieldName = getter.substring(2);
			} else {
				fieldName = getter;
			}
			return Introspector.decapitalize(fieldName);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
