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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 *
 * BeanDefinition 保存了Bean信息   例如Bean指向的类,是否是单例,是否懒加载,依赖了哪些bean
 *
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * 默认提供了两种域  singleton 和 prototype
	 *
	 * request session globalSession application websocket 这几种属于web的扩展
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * 权限相关?权限角色, 不是很重要,不用管
	 */
	int ROLE_APPLICATION = 0;
	int ROLE_SUPPORT = 1;
	int ROLE_INFRASTRUCTURE = 2;


	/**
	 * 设置父Bean,这里涉及到Bean继承,不是java继承,继承父Bean的配置信息
	 * @param parentName
	 */
	void setParentName(@Nullable String parentName);

	/**
	 * 获取父Bean
	 * @return
	 */
	@Nullable
	String getParentName();

	/**
     * 设置Bean的类名称,将来是要通过反射生成实例的
	 */
	void setBeanClassName(@Nullable String beanClassName);

	/**
	 * 获取Bean的类名称
	 * @return
	 */
	@Nullable
	String getBeanClassName();

	/**
	 * 覆盖此bean的Scope，指定一个新的Scope
	 * @param scope
	 */
	void setScope(@Nullable String scope);

	/**
	 * 获取当前Bean的Scope
	 * @return
	 */
	@Nullable
	String getScope();

	/**
	 * 设置是否开启懒加载(延迟初始化)
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 * 获取当前bean是否开始了懒加载
	 */
	boolean isLazyInit();

	/**
	 * 设置此Bean依赖的其他Bean的名称.可变参数,注意这里的依赖不是指属性依赖(autowire标记的),而是depends-on=""属性设置的值
	 */
	void setDependsOn(@Nullable String... dependsOn);

	/**
	 * 返回此Bean所有的依赖
	 */
	@Nullable
	String[] getDependsOn();

	/**
	 * 设置该Bean是否可以注入到其他Bean中(只对类型注入有效)
	 * 如果根据名称注入,即使这里设置了false,依旧能注入
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * 返回该Bean是否可以注入到其他Bean中
	 */
	boolean isAutowireCandidate();

	/**
	 * 设置是否为"主角".若设为true,同一接口的多个实现,如果不指定名称,Spring会优先选择设置primary为true的Bean
	 */
	void setPrimary(boolean primary);

	/**
	 * 返回当前是否是"主角".
	 */
	boolean isPrimary();

	/**
	 * 如果该Bean是由工厂方法生成,指定工厂名称
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);

	/**
	 * 获取工厂名称
	 */
	@Nullable
	String getFactoryBeanName();

	/**
	 * 设置工厂里的工厂方法名称
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);

	/**
	 * 获取工厂里的工厂方法名称
	 */
	@Nullable
	String getFactoryMethodName();

	/**
	 * 获取构造器参数
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * 判断是否有构造器参数
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	/**
	 * 获取Bean中的属性值
	 */
	MutablePropertyValues getPropertyValues();

	/**
	 * 判断是否有属性值
	 * @since 5.0.2
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}

	/**
	 * 判断此Bean是否是Singleton
	 */
	boolean isSingleton();

	/**
	 * 判断此Bean是否是Prototype
	 */
	boolean isPrototype();

	/**
	 * 判断此Bean是否是Abstract
	 * 一般父类继承使用,然而很少使用..
	 */
	boolean isAbstract();







	/*******************************  下边的几乎用不到   *****************************************/


	/**
	 * 获取此Bean的权限角色
	 */
	int getRole();

	/**
	 * 获取此Bean的描述(<description/> 标签)
	 */
	@Nullable
	String getDescription();

	/**
	 * 获取此Bean资源的描述
	 */
	@Nullable
	String getResourceDescription();

	/**
	 * 返回初始的bean定义，如果没有，返回null
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
