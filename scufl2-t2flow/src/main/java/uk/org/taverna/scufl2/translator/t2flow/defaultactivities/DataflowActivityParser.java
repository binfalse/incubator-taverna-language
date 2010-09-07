package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Element;

import uk.org.taverna.scufl2.api.common.ConfigurableProperty;
import uk.org.taverna.scufl2.api.configurations.ConfigurablePropertyConfiguration;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.translator.t2flow.T2Parser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.DataflowConfig;

public class DataflowActivityParser implements T2Parser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/dataflow-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.dataflow.DataflowActivity";

	public static URI scufl2Uri = URI
			.create("http://ns.taverna.org.uk/2010/activity/nested-workflow");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowActivityToURI(URI t2flowActivity) {
		return scufl2Uri;
	}

	@Override
	public Configuration parseActivityConfiguration(T2FlowParser t2FlowParser,
			ConfigBean configBean) throws ParseException {
		Object config = configBean.getAny();
		if (!(config instanceof Element)
				|| !configBean.getEncoding().equals("dataflow")) {
			throw new ParseException("Unsupported config bean " + configBean);
		}

		Unmarshaller unmarshaller2 = t2FlowParser.getUnmarshaller();
		unmarshaller2.setSchema(null);
		JAXBElement<DataflowConfig> dataflowElem;
		try {
			dataflowElem = unmarshaller2.unmarshal((Element) config,
					DataflowConfig.class);
		} catch (JAXBException e) {
			throw new ParseException("Can't parse config bean " + configBean, e);
		}

		DataflowConfig dataflowConfig = dataflowElem.getValue();
		String dataflowReference = dataflowConfig.getRef();

		Configuration configuration = new Configuration();

		ConfigurablePropertyConfiguration configurablePropertyConfiguration = new ConfigurablePropertyConfiguration();
		configurablePropertyConfiguration.setParent(configuration);
		ConfigurableProperty configuredProperty = new ConfigurableProperty(
				scufl2Uri.resolve("#workflow").toASCIIString());
		configurablePropertyConfiguration
				.setConfiguredProperty(configuredProperty);
		configurablePropertyConfiguration.setValue(dataflowReference);
		return configuration;
	}

}