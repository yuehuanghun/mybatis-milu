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
package com.yuehuanghun.mybatis.milu.criteria;

import java.util.Objects;

import com.yuehuanghun.mybatis.milu.annotation.JoinMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Join {

	private JoinMode joinMode;
	
	private Predicate joinPredicate;
	
	private String predicateExpression;
	
	public Join(JoinMode joinMode, Predicate joinPredicate) {
		this.joinMode = joinMode;
		this.joinPredicate = joinPredicate;
	}

	@Override
	public int hashCode() {
		return joinMode.hashCode() + (joinPredicate == null ? 0 : 31 * joinPredicate.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		
		if(!(obj instanceof Join)) {
			return false;
		}
		
		return joinMode == ((Join)obj).joinMode && Objects.equals(joinPredicate, ((Join)obj).joinPredicate);
	}
}
