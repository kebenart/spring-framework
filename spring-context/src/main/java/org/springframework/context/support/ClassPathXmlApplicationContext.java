/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.context.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 独立的XML应用程序上下文，从类路径中获取上下文的定义文件，将纯路径解释为
 * 包括包路径(例如，"MyPackage/myresource.txt")的类路径资源名称.
 */
public class classpathxmlapplicationcontext extends AbstractXmlApplicationContext {

	/**
	 * 配置资源数组  可以为空
	 */
	@Nullable
	private Resource[] configResources;

	public ClassPathXmlApplicationContext() {
	}

	/**
	 * 首先调用构造函数,从传递来的XML文件中自动加载信息创建上下文对象
	 * @param parent   XML文件,
	 */
	public ClassPathXmlApplicationContext(ApplicationContext parent) {
		// 加载资源解析器
		super(parent);
	}

	/**
	 * 首先调用构造函数,从传递来的XML文件中自动加载信息创建上下文对象
	 * @param configLocation   XML文件,
	 * @throws BeansException  如果上下文创建失败抛出异常
	 */
	public ClassPathXmlApplicationContext(String configLocation) throws BeansException {
		this(new String[] {configLocation}, true, null);
	}

	/**
	 * 首先调用构造函数,从传递来的XML文件中自动加载信息创建上下文对象
	 * @param configLocations   XML文件, 可以传递多个
	 * @throws BeansException  如果上下文创建失败抛出异常
	 */
	public ClassPathXmlApplicationContext(String... configLocations) throws BeansException {
		this(configLocations, true, null);
	}

	public ClassPathXmlApplicationContext(String[] configLocations, @Nullable ApplicationContext parent)
			throws BeansException {
		this(configLocations, true, parent);
	}


	public ClassPathXmlApplicationContext(String[] configLocations, boolean refresh) throws BeansException {
		this(configLocations, refresh, null);
	}


	/**
	 * 使用一个给定的父类创造一个新的ClasspathXmlApplicationContext对象
	 *
	 * @param configLocations	XML配置文件
	 * @param refresh		是否自动更新上下文,同时加载所有的Bean和创建所有的单例,或者在进一步配置上下文后手动调用Refresh,
	 * @param parent  给定的父级可以 是空的
	 * @throws BeansException  构造上下文失败抛出异常
	 */
	public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)	 //@Nullable 表示可以传递空值
			throws BeansException {
		// 调用父类构造器,传递父级,初始化资源解析器和环境
		super(parent);

		// 根据提供的路径，处理成配置文件数组(以分号、逗号、空格、tab、换行符分割)
		setConfigLocations(configLocations);

		if (refresh) {
			//更新上下文,初始化BeanFactory,加载Bean和创建所有单例
			refresh();
		}
	}


	/**
	 * 从给定的XML文件中加载定义并自动刷新上下文,这是一种相对于给定类加载路径更方便的方式.
	 *
	 * @param path 类路径中的相对路径/(绝对路径)
	 * @param clazz 要用(给定路径的基础)加载资源的类
	 */
	public ClassPathXmlApplicationContext(String path, Class<?> clazz) throws BeansException {
		this(new String[] {path}, clazz);
	}

	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz) throws BeansException {
		this(paths, clazz, null);
	}

	/**
	 * ClassPathXmlApplicationContext通过ClassPath载入Resource
	 * @param paths
	 * @param clazz
	 * @param parent
	 * @throws BeansException
	 */
	public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		Assert.notNull(paths, "Path array must not be null");
		Assert.notNull(clazz, "Class argument must not be null");
		this.configResources = new Resource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			this.configResources[i] = new ClassPathResource(paths[i], clazz);
		}
		refresh();
	}


	@Override
	@Nullable
	protected Resource[] getConfigResources() {
		return this.configResources;
	}

}
