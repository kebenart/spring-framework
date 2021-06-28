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

package org.springframework.beans.factory.xml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
\* 此类用于读取Document并注册BeanDefinition
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;

	public static final String NESTED_BEANS_ELEMENT = "beans";

	public static final String ALIAS_ELEMENT = "alias";

	public static final String NAME_ATTRIBUTE = "name";

	public static final String ALIAS_ATTRIBUTE = "alias";

	public static final String IMPORT_ELEMENT = "import";

	public static final String RESOURCE_ATTRIBUTE = "resource";

	public static final String PROFILE_ATTRIBUTE = "profile";


	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private XmlReaderContext readerContext;

	@Nullable
	private BeanDefinitionParserDelegate delegate;


	/**
	 * This implementation parses bean definitions according to the "spring-beans" XSD
	 * (or DTD, historically).
	 * <p>Opens a DOM Document; then initializes the default settings
	 * specified at the {@code <beans/>} level; then parses the contained bean definitions.
	 */
	@Override
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		this.readerContext = readerContext;
		logger.debug("Loading bean definitions");

		// 读取Document并获取根节点
		Element root = doc.getDocumentElement();

		// 从Xml根节点开始解析文件
		doRegisterBeanDefinitions(root);
	}

	/**
	 * Return the descriptor for the XML resource that this parser works on.
	 */
	protected final XmlReaderContext getReaderContext() {
		Assert.state(this.readerContext != null, "No XmlReaderContext available");
		return this.readerContext;
	}

	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor}
	 * to pull the source metadata from the supplied {@link Element}.
	 */
	@Nullable
	protected Object extractSource(Element ele) {
		return getReaderContext().extractSource(ele);
	}


	/**
	 * 在给定的根{@code <beans/>}元素中注册每个bean定义。
	 */
	protected void doRegisterBeanDefinitions(Element root) {
		// BeanDefinitionParserDelegate负责解析Bean定义,这里为啥定义一个parent,是为了后面的递归问题
		// 因为<bean/> 内部可以定义<bean/>所以这个方法的root不一定就是xml的根节点,也可能是嵌套在里面的 <beans /> 节点，从源码分析的角度，我们当做根节点就好了
		BeanDefinitionParserDelegate parent = this.delegate;

		// 为了正确地传播和保存<beans>缺省-*属性， 跟踪当前(父)委托，它可能为空. 创建新的(子)委托，其中包含对父委托的引用，用于备份，,
		this.delegate = createDelegate(getReaderContext(), root, parent);

		// 判断是否是默认命名空间(http://www.springframework.org/schema/beans)
		if (this.delegate.isDefaultNamespace(root)) {
			// 判断根节点<beans/>里的profile是否是当前环境所需要的
			// 如果当前环境配置的profile不包含此profile,那就直接return,不对bean进行解析


			// 获取根节点的运行环境(profile="")
			String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);

			// 判断是否没有设置运行环境
			if (StringUtils.hasText(profileSpec)) {
				// 通过分隔符来拆分profileSpec来获取多个运行环境(在配置多环境的情况下)
				String[] specifiedProfiles = StringUtils.tokenizeToStringArray(
						profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);

				// 若上下文中不包括当前运行环境,则打日志并结束此方法;
				if (!getReaderContext().getEnvironment().acceptsProfiles(specifiedProfiles)) {
					if (logger.isInfoEnabled()) {
						logger.info("Skipped XML bean definition file due to specified profiles [" + profileSpec +
								"] not matching: " + getReaderContext().getResource());
					}
					return;
				}
			}
		}

		/*** 模板方法模式 **/
		/***  定义一个操作中算法的框架，而将一些步骤延迟到子类中，使得子类可以不改变算法的结构即可重定义该算法中的某些特定步骤。
		 不变的部分留在abstract类中,继承该类实现abstract方法。其中子类必须实现父类的方法 **/

		/*** 钩子函数 **/
		/*** 应用程序可以在系统级对所有消息,事件进行过滤,访问在正常情况下无法访问的消息 **/
		/*** 钩子函数作为一个基本操作在模板方法之中被调用，在(重点--->)抽象基类中则为其提供一个空的或缺省的实现(<--重点)。这样一来子类可以在必要时按需进行扩展，提高了子类代码的灵活性。 **/
		/*** 举个例子，咖啡里面加入糖和奶是一个常规的操作，那么我们就不在相应的子类中不去处理钩子函数，使得算法保留了模板方法中的全部步骤。
		 * 而当我们遇到茶时，中国人不喜欢加入调料，那我们就可以在子类中覆写钩子函数，使其返回false，实现按需扩展，从而跳过模板方法中加入调料的步骤。 **/
		
		// 解析前处理,没有被用到   钩子
		preProcessXml(root);

		// 重点
		// 通过委托来进行解析BeanDefinition
		parseBeanDefinitions(root, this.delegate);

		// 解析后处理,没有被用到   钩子
		postProcessXml(root);

		this.delegate = parent;
	}

	protected BeanDefinitionParserDelegate createDelegate(
			XmlReaderContext readerContext, Element root, @Nullable BeanDefinitionParserDelegate parentDelegate) {

		// 根据ReaderContext创建委托
		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);

		// 并传递根Element和父类委托来进行初始化
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}

	/**
	 * default Namespace主要涉及到四个标签 <import/> <alias/> <bean/> 和 <beans/>, 其他都属于custom的
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		// 检测该文档是否是默认的命名空间(http://www.springframework.org/schema/beans)
		if (delegate.isDefaultNamespace(root)) {

			// 获取根节点的所有子节点
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				// 检测
				if (node instanceof Element) {
					Element ele = (Element) node;
					if (delegate.isDefaultNamespace(ele)) {

						// 处理默认的标签 <import/> <alias/> <bean/> 和 <beans/>
						parseDefaultElement(ele, delegate);
					}
					else {

						// 处理自定义标签 <mvc/> <task/> <context/> <aop/>等等
						// 这些属于扩展,如需要使用非default标签,需要在xml头部引入相应的namespace和.xsd文件路径.
						// 同时需要提供相应的parser来解析.例如MvcNamespaceHandler,TaskNamespaceHandler,ContextNamespaceHandler,AopNamespaceHandler等
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {

			// 处理自定义节点
			delegate.parseCustomElement(root);
		}
	}

	/**
	 * 对默认标签的处理
	 * @param ele
	 * @param delegate
	 */
	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		// 处理 <import/>
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		// 处理 <alias/>
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		// 重点
		// 处理 <bean/>
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}
		// 处理 beans标签
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {

			// 如果碰到的是嵌套的 <beans /> 标签，需要递归
			doRegisterBeanDefinitions(ele);
		}
	}

	/**
	 * 解析一个“Import”元素，并将bean定义从给定资源加载到bean工厂
	 */
	protected void importBeanDefinitionResource(Element ele) {
		// 获取resource属性
		String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
		// 若resource属性没有设置则报错并返回
		if (!StringUtils.hasText(location)) {
			getReaderContext().error("Resource location must not be empty", ele);
			return;
		}

		// 解析系统属性: e.g. "${user.dir}"
		location = getReaderContext().getEnvironment().resolveRequiredPlaceholders(location);

		Set<Resource> actualResources = new LinkedHashSet<>(4);

		// 判定location是绝对URI还是相对URI
		boolean absoluteLocation = false;
		try {
			absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
		}
		catch (URISyntaxException ex) {
			// 考虑到位置相对，无法转换为URI
			// 除非它是众所周知的Spring前缀“classpath*:”
		}

		// 绝对的还是相对的?
		if (absoluteLocation) {
			// 若是绝对URI则直接根据地址加载对应的配置文件
			try {
				int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
				if (logger.isDebugEnabled()) {
					logger.debug("Imported " + importCount + " bean definitions from URL location [" + location + "]");
				}
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from URL location [" + location + "]", ele, ex);
			}
		}
		else {
			// 若是相对地址则根据相对地址计算出绝地路径
			try {
				int importCount;
				// Resource存在多个子类实现类,如VfsResource,FileSystemResource等
				// 而每个resource的createRelative方法的实现都不一样,所以这里先使用子类的方法尝试解析
				Resource relativeResource = getReaderContext().getResource().createRelative(location);
				if (relativeResource.exists()) {
					importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
					actualResources.add(relativeResource);
				}
				else {
					// 如果解析不成功,则使用默认的解析器ResourcePatternResolver进行解析
					String baseLocation = getReaderContext().getResource().getURL().toString();
					importCount = getReaderContext().getReader().loadBeanDefinitions(
							StringUtils.applyRelativePath(baseLocation, location), actualResources);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Imported " + importCount + " bean definitions from relative location [" + location + "]");
				}
			}
			catch (IOException ex) {
				getReaderContext().error("Failed to resolve current resource location", ele, ex);
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to import bean definitions from relative location [" + location + "]",
						ele, ex);
			}
		}
		// 解析后通知监听器
		Resource[] actResArray = actualResources.toArray(new Resource[0]);
		getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
	}

	/**
	 * 处理给定的别名元素，向注册中心注册别名。
	 */
	protected void processAliasRegistration(Element ele) {
		// 获取beanName
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		// 获取别名
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		boolean valid = true;
		// 校验beanName是否为空
		if (!StringUtils.hasText(name)) {
			getReaderContext().error("Name must not be empty", ele);
			valid = false;
		}
		// 校验别名是否为空
		if (!StringUtils.hasText(alias)) {
			getReaderContext().error("Alias must not be empty", ele);
			valid = false;
		}
		// 校验通过
		if (valid) {
			try {
				// 进行注册别名
				getReaderContext().getRegistry().registerAlias(name, alias);
			}
			catch (Exception ex) {
				getReaderContext().error("Failed to register alias '" + alias +
						"' for bean with name '" + name + "'", ele, ex);
			}
			// 别名注册后通知监听器做相应处理
			getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
		}
	}

	/**
	 * 处理给定的bean元素，解析bean定义并在注册中心注册。
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {

		// 重点
		// 将 <bean /> 节点中的信息提取出来，然后封装到一个 BeanDefinitionHolder 中
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);

		// 若bdHolder实例不为空的
		if (bdHolder != null) {

			// 如果有自定义属性,进行相应解析
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// 重点
				// 这里是向IOC容器注册BeanDefinition
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}

			// 在BeanDefinition向IOC容器注册完以后,发送消息
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}


	/**
	 * 通过首先处理任何自定义元素类型，允许XML可扩展,
	 * 在开始处理bean定义之前. This method is a natural
	 * extension point for any other custom pre-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * @see #getReaderContext()
	 */
	protected void preProcessXml(Element root) {
	}

	/**
	 * Allow the XML to be extensible by processing any custom element types last,
	 * after we finished processing the bean definitions. This method is a natural
	 * extension point for any other custom post-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * @see #getReaderContext()
	 */
	protected void postProcessXml(Element root) {
	}

}
