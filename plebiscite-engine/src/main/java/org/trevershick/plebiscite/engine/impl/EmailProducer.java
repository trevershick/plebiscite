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

public class EmailProducer {
	public static final String SUBJECT= "subject";
	public static final String BODY = "body";
	
	public Map<String,String> buildMailMessage(String template, Map<String,Object> params) {
		
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(getClass(), "");
//		cfg.setObjectWrapper(new DefaultObjectWrapper());
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
