package org.trevershick.plebiscite.engine.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Throwables;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
/**
 * Thread-safe - this is a utility class for locating and evaluating email templates.
 * See {{@link #buildMailMessage(String, Map)} for details on params/results.
 * @author trevershick
 * @see #buildMailMessage(String, Map)
 */
public class EmailProducer {
	public static final String SUBJECT= "subject";
	public static final String BODY = "body";

	/**
	 * Evaluates the specified template (located by 'template'.body.ftl and 'template'.subject.ftl)
	 * using freemarker or other template language and returns the results as a map with two entries,
	 * one for 'subject' and one for 'body'
	 *  
	 * @param template
	 * @param params
	 * @return
	 */
	public Map<String,String> buildMailMessage(String template, Map<String,Object> params) {
		
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(getClass(), "");
		try {
			Template bodyTemplate = cfg.getTemplate(template + ".body.ftl");
			Template subjectTemplate = cfg.getTemplate(template + ".subject.ftl");
	
			Map<String,String> result = new HashMap<String, String>();
			
			result.put(BODY, processTemplate(bodyTemplate, params));
			result.put(SUBJECT, processTemplate(subjectTemplate, params));
			return result;
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	private String processTemplate(Template template, Map<String, Object> params) throws IOException,
			TemplateException {
		Writer out = new StringWriter();
		template.process(params, out);
		out.close();
		return out.toString();
	}
}
