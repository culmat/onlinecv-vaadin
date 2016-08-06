package com.beisert.onlinecv.vaadin.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Contains utility functions for working with reflection.
 * 
 * @author dbeisert
 */
public class ReflectionUtil {

	/**
	 * returns the meta data of a class by reflection. The results should be
	 * cached for performance reasons.
	 * 
	 * @param cl
	 *            theClass for which the meta data is wanted
	 * @param ignoredAttributes
	 *            some attributes that shall be ignored
	 * @return a map key=String:propertyName; value=PropertyDescriptor
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptors(Class<?> cl, String... ignoredAttributes) {

		try {
			Map<String, PropertyDescriptor> propertyDescr = new LinkedHashMap<String, PropertyDescriptor>();
			BeanInfo info = Introspector.getBeanInfo(cl);
			PropertyDescriptor[] propDesc = info.getPropertyDescriptors();

			// class property wird ignoriert
			String name = null;
			for (int i = 0; i < propDesc.length; i++) {
				name = propDesc[i].getName();
				boolean doNotIgnore = true;
				for (int j = 0; j < ignoredAttributes.length; j++) {
					if (ignoredAttributes[j].equals(name))
						doNotIgnore = false;
				}
				if (doNotIgnore) {
					propertyDescr.put(name, propDesc[i]);
				}

			}
			return propertyDescr;
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 * This utility class makes it easier to invoke a method by reflection.
	 * <br/>
	 * 
	 * <pre>
	 * Object sourceObject = "Hallo_Du";
	 * Object returnVal = new MethodInvoker(sourceObject, "split").addParam(String.class, "_").invoke();
	 * String[] result = (String[]) castArray(returnVal, String.class);
	 * 
	 * </pre>
	 * 
	 * @author beisertd
	 */
	public static class MethodInvoker {
		List<Class<?>> paramClasses = new ArrayList<Class<?>>();
		String methodName = null;
		List<Object> params = new ArrayList<Object>();
		Object objectThatHasMethod = null;
		long lastInvocationTime;

		public MethodInvoker(Object object, String methodName) {
			this.objectThatHasMethod = object;
			this.methodName = methodName;
		}

		public MethodInvoker addParam(Class<?> paramType, Object param) {
			this.paramClasses.add(paramType);
			this.params.add(param);
			return this;
		}

		/**
		 * Invoke the method and get the return value
		 * 
		 * @return the result of the invocation
		 * @throws RuntimeException
		 *             if an exception occurred
		 */
		public Object invoke() throws RuntimeException {
			long start = System.currentTimeMillis();
			try {
				Class<?> clazz = this.objectThatHasMethod.getClass();
				Class<?>[] paramTypes = (Class[]) this.paramClasses.toArray(new Class[0]);
				Method m = clazz.getMethod(this.methodName, paramTypes);
				Object result = m.invoke(this.objectThatHasMethod, this.params.toArray());
				return result;
			} catch (Exception e) {
				Exception ex = e;
				if (e instanceof InvocationTargetException) {
					ex = (Exception) e.getCause();
				}
				throw new RuntimeException("Error occurred when trying to invoke method " + this.methodName + " on "
						+ this.objectThatHasMethod, ex);
			} finally {
				this.lastInvocationTime = System.currentTimeMillis() - start;
			}

		}

		/**
		 * the time the last method invocation took
		 * 
		 * @return the time the last method invocation took
		 */
		public long getLastInvocationTime() {
			return lastInvocationTime;
		}

	}

	public static void setPropertyValue(Object bean, PropertyDescriptor desc, Object val) {
		try {
			desc.getWriteMethod().invoke(bean, val);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static Object getPropertyValue(Object bean, PropertyDescriptor desc) throws IllegalStateException {
		try {
			Object retval = desc.getReadMethod().invoke(bean, (Object[]) null);
			return retval;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Just throws runtime exceptions.
	 * @param type
	 * @return
	 */
	public static <T> T newInstance(Class<T> type) {
		try {
			return type.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	

}
