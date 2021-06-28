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

package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * 默认提供了两种域  singleton 和 prototype
	 *
	 * request session globalSession application websocket 这几种属于web的扩展
	 */
	String SCOPE_SINGLETON = "singleton";
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * 设置BeanFactory的父类
	 * @param parentBeanFactory
	 * @throws IllegalStateException
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * 设置类加载器
	 * @param beanClassLoader
	 */
	void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

	/**
	 * 获取类加载器
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * 设置用于类型匹配目的的临时类加载器。
	 */
	void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

	/**
	 * 返回临时类加载器
	 */
	@Nullable
	ClassLoader getTempClassLoader();

	/**
	 * 设置是否缓存bean元数据
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * 返回是否缓存bean元数据
	 */
	boolean isCacheBeanMetadata();

	/**
	 * 设置Bean表达式指定解析类
	 */
	void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

	/**
	 * 获取Bean表达式指定解析类
	 */
	@Nullable
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * 设置用于转换属性值的Spring 3.0转换服务，作为javabean propertyeditor的替代方案。
	 */
	void setConversionService(@Nullable ConversionService conversionService);

	/**
	 * 获取转换服务
	 */
	@Nullable
	ConversionService getConversionService();

	/**
	 * 添加要应用于所有bean创建过程的PropertyEditorRegistry
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * 为指定类型的所有属性注册指定的自定义属性编辑器
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * 使用自定义编辑器帽初始化给定的PropertyEditorRegistry已经在这个BeanFactory中注册
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * 设置一个自定义类型转换器，此BeanFactory应该使用该转换器进行转换 bean属性值、构造函数参数值等。
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * 获取此BeanFactory所使用的类型转换器。.这可能是每个调用的一个新实例，因为typeconverter通常是而不是线程安全的
	 */
	TypeConverter getTypeConverter();

	/**
	 * 为嵌入的值(如注释属性)添加字符串解析器。
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * 确定是否已向此bean工厂注册了嵌入式值解析器
	 */
	boolean hasEmbeddedValueResolver();

	/**
	 * 解析给定的嵌入值，例如注释属性
	 */
	@Nullable
	String resolveEmbeddedValue(String value);

	/**
	 * 添加一个新的BeanPostProcessor，它将应用于这个工厂创建的bean.在 工厂配置期间调用。
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * 获取BeanPostProcessor的个数
	 */
	int getBeanPostProcessorCount();

	/**
	 * 注册由给定范围实现支持的给定范围。
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * 返回所有当前注册范围的名称。
	 */
	String[] getRegisteredScopeNames();

	/**
	 * 返回给定范围名称的范围实现(
	 */
	@Nullable
	Scope getRegisteredScope(String scopeName);

	/**
	 * 提供与此工厂相关的安全访问控制上下文。
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * 从给定的其他工厂复制所有相关配置。
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * 给定bean名称，创建别名。我们通常使用这个方法来支持XML id中非法的名称(用于bean名称)。
	 * 通常在工厂配置期间调用，但也可以调用于别名的运行时注册
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * 解析在此工厂注册的所有别名目标名称和别名，并将给定的StringValueResolver应用于它们。
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * 返回给定bean名称的合并bean定义，必要时将子bean定义与其父bean定义合并
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 确定具有给定名称的bean是否为FactoryBean
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 显式控制指定bean的当前创建状态
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * 确定指定的bean当前是否在创建中
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * 为给定bean注册一个依赖bean,在给定bean被销毁之前被销毁
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * 返回依赖于指定bean的所有bean的名称(如果有的话)
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * 返回指定bean所依赖的所有bean的名称(如果有的话)
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * 根据bean定义销毁给定的bean实例(通常是从该工厂获得的原型实例)
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * 销毁当前目标作用域中指定作用域的bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * 销毁工厂中的所有单例bean，包括已注册为一次性的内部bean
	 */
	void destroySingletons();

}
