/*
 * Copyright 2020-2023 the original author or authors.
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
package com.yuehuanghun.mybatis.milu.tool;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.Value;

/**
 * reference org.springframework.data.util.LazyStreamable
 * 
 * @author yuehuanghun
 *
 * @param <T>
 */
@Value(staticConstructor = "of")
class LazyStreamable<T> implements Streamable<T> {

	private final Supplier<? extends Stream<T>> stream;

	@Override
	public Iterator<T> iterator() {
		return stream().iterator();
	}

	@Override
	public Stream<T> stream() {
		return stream.get();
	}
}
