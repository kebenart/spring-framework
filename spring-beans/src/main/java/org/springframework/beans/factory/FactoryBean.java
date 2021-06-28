/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

/**
 *
 *
 */
public interface FactoryBean<T> {

	/**
	 * 返回由FactoryBean创建的bean实例,若isSingleton()返回true,则该实例会放到Spring容器中单实例缓存池中
	 */
	@Nullable
	T getObject() throws Exception;

	/**
	 * 返回FactoryBean创建的Bean类型
	 */
	@Nullable
	Class<?> getObjectType();

	/**
	 * 返回由FactoryBean创建的bean实例的作用域是singleton还是prototype
	 * @return
	 */
	default boolean isSingleton() {
		return true;
	}

}
