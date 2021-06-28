/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.core.io;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * 加载资源的策略接口(e..类路径或文件系统 参考资料)
 * An {@link org.springframework.context.ApplicationContext}
 * 是否需要提供此功能，以及扩展 (ResourcePatternResolver)
 * DefaultResourceLoader 是一个独立的实现 ,它在ApplicationContext外也可使用, 同时也被ResourceEditor使用
 * <p>可以填充资源类型和资源数组的Bean属性,在应用程序上下文中运行时,使用特定的上下文的资源加载策略。
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see Resource
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

	/** 伪URL前缀，用于从类路径加载:“classpath:” */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * 返回指定资源位置的资源句柄。
	 * <p>句柄应该始终是一个可重用的资源描述符，
	 * 允许多个{getInputStream()调用。
	 * <li>必须支持完全限定的url, e.g. "file:C:/test.dat".
	 * <li>必须支持类路径伪url, e.g. "classpath:test.dat".
	 * <li>应该支持相对文件路径, e.g. "WEB-INF/test.dat".
	 * (这将是特定于实现的，通常由ApplicationContext的实现。)
	 * <p>注意，资源句柄并不意味着现有资源;
	 * 你需要调用exists去检测资源是否存在
	 *
	 * @param location the resource location
	 * @return 返回对应的资源句柄(绝不为空)
	 * @see #CLASSPATH_URL_PREFIX
	 * @see Resource#exists()
	 * @see Resource#getInputStream()
	 */
	Resource getResource(String location);

	/**
	 * 公开这个ResourceLoader使用的类加载器。
	 * <p>需要直接访问类加载器的客户机可以这样做与ResourceLoader保持一致,而不是依赖线程上下文加载器
	 * @return the ClassLoader
	 * (只有在系统类加载器不可访问时才使用)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
	 */
	@Nullable
	ClassLoader getClassLoader();

}
