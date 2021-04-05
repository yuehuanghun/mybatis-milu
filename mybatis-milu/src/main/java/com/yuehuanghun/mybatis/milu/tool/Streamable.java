/*
 * Copyright 2020-2021 the original author or authors.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * reference org.springframework.data.util.Streamable
 * 
 * @author yuehuanghun
 */
@FunctionalInterface
public interface Streamable<T> extends Iterable<T> {
	
	static <T> Streamable<T> empty() {
		return Collections::emptyIterator;
	}

	@SafeVarargs
	static <T> Streamable<T> of(T... t) {
		return () -> Arrays.asList(t).iterator();
	}

	static <T> Streamable<T> of(Iterable<T> iterable) {

		Assert.notNull(iterable, "Iterable must not be null!");

		return iterable::iterator;
	}

	static <T> Streamable<T> of(Supplier<? extends Stream<T>> supplier) {
		return LazyStreamable.of(supplier);
	}

	default Stream<T> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default <R> Streamable<R> map(Function<? super T, ? extends R> mapper) {

		Assert.notNull(mapper, "Mapping function must not be null!");

		return Streamable.of(() -> stream().map(mapper));
	}

	default <R> Streamable<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {

		Assert.notNull(mapper, "Mapping function must not be null!");

		return Streamable.of(() -> stream().flatMap(mapper));
	}

	default Streamable<T> filter(Predicate<? super T> predicate) {

		Assert.notNull(predicate, "Filter predicate must not be null!");

		return Streamable.of(() -> stream().filter(predicate));
	}
}