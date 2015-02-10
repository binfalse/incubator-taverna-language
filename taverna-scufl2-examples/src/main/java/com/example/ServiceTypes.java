package com.example;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.api.profiles.Profile;

public class ServiceTypes {
	public static void main(String[] args) throws Exception, ReaderException,
			WriterException {
		for (String type :  new ServiceTypes().serviceTypes(args)) {
			System.out.println(type);
		}
	}

	public Set<String> serviceTypes(String[] filepaths) throws ReaderException, IOException, WriterException {
		
		Set<String> types = new LinkedHashSet<String>();
		
		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String filepath : filepaths) {
			File file = new File(filepath);
			// mediaType = null  --> guess
			WorkflowBundle wfBundle = io.readBundle(file, null);
			
			for (Profile profile : wfBundle.getProfiles()) {
				for (Activity activity : profile.getActivities()) {
					types.add(activity.getType().toASCIIString());
				}
			}			
		}
		return types;
	}

}
