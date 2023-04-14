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
package com.yuehuanghun.mybatis.milu.exception;

public class OrmBuildingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OrmBuildingException() {
		super();
	}

	public OrmBuildingException(String error) {
		super(error);
	}
	
	public OrmBuildingException(Throwable e) {
		super(e);
	}
	
	public OrmBuildingException(String error, Throwable e) {
		super(error, e);
	}
}
