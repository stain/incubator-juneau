// ***************************************************************************************************************************
// * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file *
// * distributed with this work for additional information regarding copyright ownership.  The ASF licenses this file        *
// * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance            *
// * with the License.  You may obtain a copy of the License at                                                              *
// *                                                                                                                         *
// *  http://www.apache.org/licenses/LICENSE-2.0                                                                             *
// *                                                                                                                         *
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an  *
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the        *
// * specific language governing permissions and limitations under the License.                                              *
// ***************************************************************************************************************************
package org.apache.juneau.serializer;

import static org.apache.juneau.internal.ClassUtils.*;
import static org.apache.juneau.serializer.SerializerContext.*;

import java.io.*;
import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import org.apache.juneau.*;
import org.apache.juneau.internal.*;
import org.apache.juneau.transform.*;

/**
 * Context object that lives for the duration of a single use of {@link Serializer}.
 * <p>
 * Used by serializers for the following purposes:
 * <ul class='spaced-list'>
 * 	<li>Keeping track of how deep it is in a model for indentation purposes.
 * 	<li>Ensuring infinite loops don't occur by setting a limit on how deep to traverse a model.
 * 	<li>Ensuring infinite loops don't occur from loops in the model (when detectRecursions is enabled.
 * 	<li>Allowing serializer properties to be overridden on method calls.
 * </ul>
 * <p>
 * This class is NOT thread safe.  It is meant to be discarded after one-time use.
 *
 * @author James Bognar (james.bognar@salesforce.com)
 */
public class SerializerSession extends Session {

	private static JuneauLogger logger = JuneauLogger.getLogger(SerializerSession.class);

	private final int maxDepth, initialDepth;
	private final boolean
		debug,
		detectRecursions,
		ignoreRecursions,
		useIndentation,
		addBeanTypeProperties,
		trimNulls,
		trimEmptyCollections,
		trimEmptyMaps,
		trimStrings,
		sortCollections,
		sortMaps;
	private final char quoteChar;
	private final String relativeUriBase, absolutePathUriBase;

	private final ObjectMap overrideProperties;

	/** The current indentation depth into the model. */
	public int indent;

	private boolean closed;
	private final Map<Object,Object> set;                                           // Contains the current objects in the current branch of the model.
	private final LinkedList<StackElement> stack = new LinkedList<StackElement>();  // Contains the current objects in the current branch of the model.
	private boolean isBottom;                                                       // If 'true', then we're at a leaf in the model (i.e. a String, Number, Boolean, or null).
	private final List<String> warnings = new LinkedList<String>();                 // Any warnings encountered.
	private final BeanContext beanContext;                                          // The bean context being used for this session.
	private final Method javaMethod;                                                // Java method that invoked this serializer.
	private final Object output;
	private OutputStream outputStream;
	private Writer writer, flushOnlyWriter;
	private BeanPropertyMeta currentProperty;
	private ClassMeta<?> currentClass;


	/**
	 * Create a new session using properties specified in the context.
	 *
	 * @param ctx The context creating this session object.
	 * 	The context contains all the configuration settings for this object.
	 * @param beanContext The bean context being used.
	 * @param output The output object.
	 * 	<br>Character-based serializers can handle the following output class types:
	 * 	<ul>
	 * 		<li>{@link Writer}
	 * 		<li>{@link OutputStream} - Output will be written as UTF-8 encoded stream.
	 * 		<li>{@link File} - Output will be written as system-default encoded stream.
	 * 	</ul>
	 * 	<br>Stream-based serializers can handle the following output class types:
	 * 	<ul>
	 * 		<li>{@link OutputStream}
	 * 		<li>{@link File}
	 * 	</ul>
	 * @param op The override properties.
	 * 	These override any context properties defined in the context.
	 * @param javaMethod The java method that called this parser, usually the method in a REST servlet.
	 */
	public SerializerSession(SerializerContext ctx, BeanContext beanContext, Object output, ObjectMap op, Method javaMethod) {
		super(ctx);
		this.beanContext = beanContext;
		this.javaMethod = javaMethod;
		this.output = output;
		if (op == null || op.isEmpty()) {
			overrideProperties = new ObjectMap();
			maxDepth = ctx.maxDepth;
			initialDepth = ctx.initialDepth;
			debug = ctx.debug;
			detectRecursions = ctx.detectRecursions;
			ignoreRecursions = ctx.ignoreRecursions;
			useIndentation = ctx.useIndentation;
			addBeanTypeProperties = ctx.addBeanTypeProperties;
			trimNulls = ctx.trimNulls;
			trimEmptyCollections = ctx.trimEmptyCollections;
			trimEmptyMaps = ctx.trimEmptyMaps;
			trimStrings = ctx.trimStrings;
			quoteChar = ctx.quoteChar;
			relativeUriBase = ctx.relativeUriBase;
			absolutePathUriBase = ctx.absolutePathUriBase;
			sortCollections = ctx.sortCollections;
			sortMaps = ctx.sortMaps;
		} else {
			overrideProperties = op;
			maxDepth = op.getInt(SERIALIZER_maxDepth, ctx.maxDepth);
			initialDepth = op.getInt(SERIALIZER_initialDepth, ctx.initialDepth);
			debug = op.getBoolean(SERIALIZER_debug, ctx.debug);
			detectRecursions = op.getBoolean(SERIALIZER_detectRecursions, ctx.detectRecursions);
			ignoreRecursions = op.getBoolean(SERIALIZER_ignoreRecursions, ctx.ignoreRecursions);
			useIndentation = op.getBoolean(SERIALIZER_useIndentation, ctx.useIndentation);
			addBeanTypeProperties = op.getBoolean(SERIALIZER_addBeanTypeProperties, ctx.addBeanTypeProperties);
			trimNulls = op.getBoolean(SERIALIZER_trimNullProperties, ctx.trimNulls);
			trimEmptyCollections = op.getBoolean(SERIALIZER_trimEmptyCollections, ctx.trimEmptyCollections);
			trimEmptyMaps = op.getBoolean(SERIALIZER_trimEmptyMaps, ctx.trimEmptyMaps);
			trimStrings = op.getBoolean(SERIALIZER_trimStrings, ctx.trimStrings);
			quoteChar = op.getString(SERIALIZER_quoteChar, ""+ctx.quoteChar).charAt(0);
			relativeUriBase = op.getString(SERIALIZER_relativeUriBase, ctx.relativeUriBase);
			absolutePathUriBase = op.getString(SERIALIZER_absolutePathUriBase, ctx.absolutePathUriBase);
			sortCollections = op.getBoolean(SERIALIZER_sortCollections, ctx.sortMaps);
			sortMaps = op.getBoolean(SERIALIZER_sortMaps, ctx.sortMaps);
		}

		this.indent = initialDepth;
		if (detectRecursions || debug) {
			set = new IdentityHashMap<Object,Object>();
		} else {
			set = Collections.emptyMap();
		}
	}

	/**
	 * Wraps the specified output object inside an output stream.
	 * Subclasses can override this method to implement their own specialized output streams.
	 * <p>
	 * This method can be used if the output object is any of the following class types:
	 * <ul>
	 * 	<li>{@link OutputStream}
	 * 	<li>{@link File}
	 * </ul>
	 *
	 * @return The output object wrapped in an output stream.
	 * @throws Exception If object could not be converted to an output stream.
	 */
	public OutputStream getOutputStream() throws Exception {
		if (output == null)
			throw new SerializeException("Output cannot be null.");
		if (output instanceof OutputStream)
			return (OutputStream)output;
		if (output instanceof File) {
			if (outputStream == null)
				outputStream = new BufferedOutputStream(new FileOutputStream((File)output));
			return outputStream;
		}
		throw new SerializeException("Cannot convert object of type {0} to an OutputStream.", output.getClass().getName());
	}


	/**
	 * Wraps the specified output object inside a writer.
	 * Subclasses can override this method to implement their own specialized writers.
	 * <p>
	 * This method can be used if the output object is any of the following class types:
	 * <ul>
	 * 	<li>{@link Writer}
	 * 	<li>{@link OutputStream} - Output will be written as UTF-8 encoded stream.
	 * 	<li>{@link File} - Output will be written as system-default encoded stream.
	 * </ul>
	 *
	 * @return The output object wrapped in a Writer.
	 * @throws Exception If object could not be converted to a writer.
	 */
	public Writer getWriter() throws Exception {
		if (output == null)
			throw new SerializeException("Output cannot be null.");
		if (output instanceof Writer)
			return (Writer)output;
		if (output instanceof OutputStream) {
			if (flushOnlyWriter == null)
				flushOnlyWriter = new OutputStreamWriter((OutputStream)output, IOUtils.UTF8);
			return flushOnlyWriter;
		}
		if (output instanceof File) {
			if (writer == null)
				writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream((File)output)));
			return writer;
		}
		throw new SerializeException("Cannot convert object of type {0} to a Writer.", output.getClass().getName());
	}

	/**
	 * Returns the raw output object passed into this session.
	 *
	 * @return The raw output object passed into this session.
	 */
	protected Object getOutput() {
		return output;
	}

	/**
	 * Sets the current bean property being serialized for proper error messages.
	 * @param currentProperty The current property being serialized.
	 */
	public void setCurrentProperty(BeanPropertyMeta currentProperty) {
		this.currentProperty = currentProperty;
	}

	/**
	 * Sets the current class being serialized for proper error messages.
	 * @param currentClass The current class being serialized.
	 */
	public void setCurrentClass(ClassMeta<?> currentClass) {
		this.currentClass = currentClass;
	}

	/**
	 * Returns the bean context in use for this session.
	 *
	 * @return The bean context in use for this session.
	 */
	public final BeanContext getBeanContext() {
		return beanContext;
	}

	/**
	 * Returns the Java method that invoked this serializer.
	 * <p>
	 * When using the REST API, this is the Java method invoked by the REST call.
	 * Can be used to access annotations defined on the method or class.
	 *
	 * @return The Java method that invoked this serializer.
	*/
	public final Method getJavaMethod() {
		return javaMethod;
	}

	/**
	 * Returns the override properties passed in to the constructor.
	 *
	 * @return The override properties pass in to the constructor.
	 */
	public final ObjectMap getProperties() {
		return overrideProperties;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_maxDepth} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_maxDepth} setting value for this session.
	 */
	public final int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_initialDepth} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_initialDepth} setting value for this session.
	 */
	public final int getInitialDepth() {
		return initialDepth;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_debug} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_debug} setting value for this session.
	 */
	public final boolean isDebug() {
		return debug;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_detectRecursions} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_detectRecursions} setting value for this session.
	 */
	public final boolean isDetectRecursions() {
		return detectRecursions;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_ignoreRecursions} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_ignoreRecursions} setting value for this session.
	 */
	public final boolean isIgnoreRecursions() {
		return ignoreRecursions;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_useIndentation} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_useIndentation} setting value for this session.
	 */
	public final boolean isUseIndentation() {
		return useIndentation;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_addBeanTypeProperties} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_addBeanTypeProperties} setting value for this session.
	 */
	public final boolean isAddBeanTypeProperties() {
		return addBeanTypeProperties;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_quoteChar} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_quoteChar} setting value for this session.
	 */
	public final char getQuoteChar() {
		return quoteChar;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_trimNullProperties} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_trimNullProperties} setting value for this session.
	 */
	public final boolean isTrimNulls() {
		return trimNulls;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_trimEmptyCollections} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_trimEmptyCollections} setting value for this session.
	 */
	public final boolean isTrimEmptyCollections() {
		return trimEmptyCollections;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_trimEmptyMaps} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_trimEmptyMaps} setting value for this session.
	 */
	public final boolean isTrimEmptyMaps() {
		return trimEmptyMaps;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_trimStrings} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_trimStrings} setting value for this session.
	 */
	public final boolean isTrimStrings() {
		return trimStrings;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_sortCollections} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_sortCollections} setting value for this session.
	 */
	public final boolean isSortCollections() {
		return sortCollections;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_sortMaps} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_sortMaps} setting value for this session.
	 */
	public final boolean isSortMaps() {
		return sortMaps;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_relativeUriBase} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_relativeUriBase} setting value for this session.
	 */
	public final String getRelativeUriBase() {
		return relativeUriBase;
	}

	/**
	 * Returns the {@link SerializerContext#SERIALIZER_absolutePathUriBase} setting value for this session.
	 *
	 * @return The {@link SerializerContext#SERIALIZER_absolutePathUriBase} setting value for this session.
	 */
	public final String getAbsolutePathUriBase() {
		return absolutePathUriBase;
	}

	/**
	 * Push the specified object onto the stack.
	 *
	 * @param attrName The attribute name.
	 * @param o The current object being serialized.
	 * @param eType The expected class type.
	 * @return The {@link ClassMeta} of the object so that <code>instanceof</code> operations
	 * 	only need to be performed once (since they can be expensive).<br>
	 * @throws SerializeException If recursion occurred.
	 */
	public ClassMeta<?> push(String attrName, Object o, ClassMeta<?> eType) throws SerializeException {
		indent++;
		isBottom = true;
		if (o == null)
			return null;
		Class<?> c = o.getClass();
		ClassMeta<?> cm = (eType != null && c == eType.getInnerClass()) ? eType : beanContext.getClassMeta(c);
		if (cm.isCharSequence() || cm.isNumber() || cm.isBoolean())
			return cm;
		if (detectRecursions || debug) {
			if (stack.size() > maxDepth)
				return null;
			if (willRecurse(attrName, o, cm))
				return null;
			isBottom = false;
			stack.add(new StackElement(stack.size(), attrName, o, cm));
			if (debug)
				logger.info(getStack(false));
			set.put(o, o);
		}
		return cm;
	}

	/**
	 * Returns <jk>true</jk> if {@link SerializerContext#SERIALIZER_detectRecursions} is enabled, and the specified
	 * 	object is already higher up in the serialization chain.
	 *
	 * @param attrName The bean property attribute name, or some other identifier.
	 * @param o The object to check for recursion.
	 * @param cm The metadata on the object class.
	 * @return <jk>true</jk> if recursion detected.
	 * @throws SerializeException If recursion occurred.
	 */
	public boolean willRecurse(String attrName, Object o, ClassMeta<?> cm) throws SerializeException {
		if (! (detectRecursions || debug))
			return false;
		if (! set.containsKey(o))
			return false;
		if (ignoreRecursions && ! debug)
			return true;

		stack.add(new StackElement(stack.size(), attrName, o, cm));
		throw new SerializeException("Recursion occurred, stack={0}", getStack(true));
	}

	/**
	 * Pop an object off the stack.
	 */
	public void pop() {
		indent--;
		if ((detectRecursions || debug) && ! isBottom)  {
			Object o = stack.removeLast().o;
			Object o2 = set.remove(o);
			if (o2 == null)
				addWarning("Couldn't remove object of type ''{0}'' on attribute ''{1}'' from object stack.", o.getClass().getName(), stack);
		}
		isBottom = false;
	}

	/**
	 * The current indentation depth.
	 *
	 * @return The current indentation depth.
	 */
	public int getIndent() {
		return indent;
	}

	/**
	 * Logs a warning message.
	 *
	 * @param msg The warning message.
	 * @param args Optional printf arguments to replace in the error message.
	 */
	public void addWarning(String msg, Object... args) {
		logger.warning(msg, args);
		msg = args.length == 0 ? msg : MessageFormat.format(msg, args);
		warnings.add((warnings.size() + 1) + ": " + msg);
	}

	/**
	 * Specialized warning when an exception is thrown while executing a bean getter.
	 *
	 * @param p The bean map entry representing the bean property.
	 * @param t The throwable that the bean getter threw.
	 */
	public void addBeanGetterWarning(BeanPropertyMeta p, Throwable t) {
		String prefix = (debug ? getStack(false) + ": " : "");
		addWarning("{0}Could not call getValue() on property ''{1}'' of class ''{2}'', exception = {3}", prefix, p.getName(), p.getBeanMeta().getClassMeta(), t.getLocalizedMessage());
	}

	/**
	 * Trims the specified string if {@link SerializerSession#isTrimStrings()} returns <jk>true</jk>.
	 *
	 * @param o The input string to trim.
	 * @return The trimmed string, or <jk>null</jk> if the input was <jk>null</jk>.
	 */
	public final String trim(Object o) {
		if (o == null)
			return null;
		String s = o.toString();
		if (trimStrings)
			s = s.trim();
		return s;
	}

	/**
	 * Generalize the specified object if a POJO swap is associated with it.
	 *
	 * @param o The object to generalize.
	 * @param type The type of object.
	 * @return The generalized object, or <jk>null</jk> if the object is <jk>null</jk>.
	 * @throws SerializeException If a problem occurred trying to convert the output.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final Object generalize(Object o, ClassMeta<?> type) throws SerializeException {
		if (o == null)
			return null;
		PojoSwap f = (type == null || type.isObject() ? getBeanContext().getClassMeta(o.getClass()).getPojoSwap() : type.getPojoSwap());
		if (f == null)
			return o;
		return f.swap(o, getBeanContext());
	}

	/**
	 * Returns <jk>true</jk> if the specified value should not be serialized.
	 *
	 * @param cm The class type of the object being serialized.
	 * @param attrName The bean attribute name, or <jk>null</jk> if this isn't a bean attribute.
	 * @param value The object being serialized.
	 * @return <jk>true</jk> if the specified value should not be serialized.
	 * @throws SerializeException If recursion occurred.
	 */
	public final boolean canIgnoreValue(ClassMeta<?> cm, String attrName, Object value) throws SerializeException {

		if (trimNulls && value == null)
			return true;

		if (value == null)
			return false;

		if (cm == null)
			cm = getBeanContext().object();

		if (trimEmptyCollections) {
			if (cm.isArray() || (cm.isObject() && value.getClass().isArray())) {
				if (((Object[])value).length == 0)
					return true;
			}
			if (cm.isCollection() || (cm.isObject() && isParentClass(Collection.class, value.getClass()))) {
				if (((Collection<?>)value).isEmpty())
					return true;
			}
		}

		if (trimEmptyMaps) {
			if (cm.isMap() || (cm.isObject() && isParentClass(Map.class, value.getClass()))) {
				if (((Map<?,?>)value).isEmpty())
					return true;
			}
		}

		if (trimNulls && willRecurse(attrName, value, cm))
			return true;

		return false;
	}

	/**
	 * Sorts the specified map if {@link SerializerSession#isSortMaps()} returns <jk>true</jk>.
	 *
	 * @param m The map being sorted.
	 * @return A new sorted {@link TreeMap}.
	 */
	public final <K,V> Map<K,V> sort(Map<K,V> m) {
		if (sortMaps && m != null && (! m.isEmpty()) && m.keySet().iterator().next() instanceof Comparable<?>)
			return new TreeMap<K,V>(m);
		return m;
	}

	/**
	 * Sorts the specified collection if {@link SerializerSession#isSortCollections()} returns <jk>true</jk>.
	 *
	 * @param c The collection being sorted.
	 * @return A new sorted {@link TreeSet}.
	 */
	public final <E> Collection<E> sort(Collection<E> c) {
		if (sortCollections && c != null && (! c.isEmpty()) && c.iterator().next() instanceof Comparable<?>)
			return new TreeSet<E>(c);
		return c;
	}

	/**
	 * Converts a String to an absolute URI based on the {@link SerializerContext#SERIALIZER_absolutePathUriBase} and
	 * 	{@link SerializerContext#SERIALIZER_relativeUriBase} settings on this context.
	 *
	 * @param uri The input URI.
	 * @return The resolved URI.
	 */
	public String resolveUri(String uri) {
		if (uri.indexOf("://") != -1 || (absolutePathUriBase == null && relativeUriBase == null))
			return uri;
		StringBuilder sb = new StringBuilder(
			uri.length()
			+ (absolutePathUriBase == null ? 0 : absolutePathUriBase.length())
			+ 1
			+ (relativeUriBase == null ? 0 : relativeUriBase.length())
		);
		if (StringUtils.startsWith(uri, '/')) {
			if (absolutePathUriBase != null)
				sb.append(absolutePathUriBase);
		} else {
			if (relativeUriBase != null) {
				sb.append(relativeUriBase);
				if (! uri.equals("/"))
					sb.append("/");
			}
		}
		sb.append(uri);
		return sb.toString();
	}

	/**
	 * Converts the specified object to a <code>String</code>.
	 *
	 * @param o The object to convert to a <code>String</code>.
	 * @return The
	 */
	public String toString(Object o) {
		if (o == null)
			return null;
		if (o.getClass() == Class.class)
			return ClassUtils.getReadableClassName((Class<?>)o);
		String s = o.toString();
		if (trimStrings)
			s = s.trim();
		return s;
	}

	/**
	 * Perform cleanup on this context object if necessary.
	 *
	 * @throws SerializeException If we're in debug mode and one or more warnings occurred.
	 */
	public void close() throws SerializeException {
		if (closed)
			throw new SerializeException("Attempt to close SerializerSession more than once.");

		try {
			if (outputStream != null)
				outputStream.close();
			if (flushOnlyWriter != null)
				flushOnlyWriter.flush();
			if (writer != null)
				writer.close();
		} catch (IOException e) {
			throw new SerializeException(e);
		}

		if (debug && warnings.size() > 0)
			throw new SerializeException("Warnings occurred during serialization: \n" + StringUtils.join(warnings, "\n"));

		closed = true;
	}

	@Override /* Object */
	protected void finalize() throws Throwable {
		if (! closed)
			throw new RuntimeException("SerializerSession was not closed.");
	}

	private static class StackElement {
		private int depth;
		private String name;
		private Object o;
		private ClassMeta<?> aType;

		private StackElement(int depth, String name, Object o, ClassMeta<?> aType) {
			this.depth = depth;
			this.name = name;
			this.o = o;
			this.aType = aType;
		}

		private String toString(boolean simple) {
			StringBuilder sb = new StringBuilder().append('[').append(depth).append(']');
			sb.append(StringUtils.isEmpty(name) ? "<noname>" : name).append(':');
			sb.append(aType.toString(simple));
			if (aType != aType.getSerializedClassMeta())
				sb.append('/').append(aType.getSerializedClassMeta().toString(simple));
			return sb.toString();
		}
	}

	private String getStack(boolean full) {
		StringBuilder sb = new StringBuilder();
		for (StackElement e : stack) {
			if (full) {
				sb.append("\n\t");
				for (int i = 1; i < e.depth; i++)
					sb.append("  ");
				if (e.depth > 0)
					sb.append("->");
				sb.append(e.toString(false));
			} else {
				sb.append(" > ").append(e.toString(true));
			}
		}
		return sb.toString();
	}

	/**
	 * Returns information used to determine at what location in the parse a failure occurred.
	 *
	 * @return A map, typically containing something like <code>{line:123,column:456,currentProperty:"foobar"}</code>
	 */
	public Map<String,Object> getLastLocation() {
		Map<String,Object> m = new LinkedHashMap<String,Object>();
		if (currentClass != null)
			m.put("currentClass", currentClass);
		if (currentProperty != null)
			m.put("currentProperty", currentProperty);
		if (stack != null && ! stack.isEmpty())
			m.put("stack", stack);
		return m;
	}

	/**
	 * Create a "_type" property that contains the dictionary name of the bean.
	 *
	 * @param m
	 * 	The bean map to create a class property on.
	 * @return
	 * 	A new bean property value.
	 */
	public BeanPropertyValue createBeanTypeNameProperty(BeanMap<?> m) {
		BeanMeta<?> bm = m.getMeta();
		String name = bm.getClassMeta().getDictionaryName();
		if (name == null)
			return null;
		return new BeanPropertyValue(bm.getTypeProperty(), name, null);
	}
}
