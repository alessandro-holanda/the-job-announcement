package com.plexiti.activiti.test.mocking;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.mockito.Mock;

import org.camunda.bpm.engine.test.mock.Mocks;

public class Mockitos {

	private static Logger log = Logger.getLogger(Mockitos.class.getName());

	public static void register(Object junitTest)
	{
		Class<?> clazz = junitTest.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (int i=0; i<fields.length; i++ ) {
			final Mock annotation = fields[i].getAnnotation(Mock.class);
			final boolean isAnnotated = annotation != null;
			final String classNameContains = "Mockito";
			try {
				Object value = fields[i].get(junitTest);
				if (value == null && isAnnotated) {
					throw new RuntimeException("Could not retrieve mock object bound to field '" + fields[i].getName() + "' of test class '" + clazz.getSimpleName() + "'. Value is null.");					
				} else if (value == null) {
					log.info("Could not inspect object bound to field '" + fields[i].getName() + "' of test class '" + clazz.getSimpleName() + "'. Value is null.");
				} else if (isAnnotated || value.getClass().getName().contains(classNameContains)) {
					String key = isAnnotated && annotation.name() != null && !annotation.name().equals("") ? annotation.name() : fields[i].getName();
					log.info("Due to " + (isAnnotated ? "Annotation" : "class name containing expected string '" + classNameContains + "'") + " registered object bound to field '" + key + "' of test class '" + clazz.getSimpleName() + "' as mock associated with key '" + key + "'.");
					Mocks.register(key, value);
				} else {
					log.info("Did not register object bound to field '" + fields[i].getName() + "' of test class '" + clazz.getSimpleName() + "' as mock. Class name does not contain the expected string '" + classNameContains + "'.");
				}
			} catch (IllegalAccessException e) {
				if (isAnnotated) {
					throw new RuntimeException("Could not retrieve mock object bound to field '" + fields[i].getName() + "' of test class '" + clazz.getSimpleName() + "'. Field must be declared as public.", e);
				}
				log.warning("Could not inspect object bound to field '" + fields[i].getName() + "' of test class '" + clazz.getSimpleName() + "'. Field is not accessible.");
			}
		}
	}

}
