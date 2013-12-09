package com.splunk.modularinput.java;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ModularInputWizardPage extends WizardPage {
	Map<String, String> options;
	Map<String, String> errorMessages = new HashMap<String, String>();

	public ModularInputWizardPage(Map<String, String> options) {
		super("New Splunk modular input in Java");
		this.options = options;
		setTitle("New Splunk modular input in Java");
		setDescription("Create a new project for developing Splunk modular inputs.");
	}
	
	public class ProjectSettings extends Composite {

		public ProjectSettings(Composite parent, int style) {
			super(parent, style);
			setLayout(new GridLayout(1, true));
			
			
		}
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		composite.setLayout(new GridLayout(1, true));
		
		/* Project name */
		final LabelledWidget<Text> projectName = new LabelledWidget<Text>(
			composite,
			"Project name:",
			"(Name can only contain letters, numbers, '.', and '_'.)",
			new WidgetCreation<Text>() {
				public Text init(Composite parent) {
					return new Text(parent, SWT.SINGLE | SWT.BORDER);
				}
			},
			SWT.NONE
		);
		projectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* Location */
		final ToggledWidget<LabelledWidget<DirectoryChooser>> location = new ToggledWidget<LabelledWidget<DirectoryChooser>>(
				composite,
				"Use default location",
				true,
				false,
				new WidgetCreation<LabelledWidget<DirectoryChooser>>() {
					public LabelledWidget<DirectoryChooser> init(Composite parent) {
						return new LabelledWidget<DirectoryChooser>(
								parent,
								"Location:",
								null,
								new WidgetCreation<DirectoryChooser>() {
									public DirectoryChooser init(Composite parent) {
										return new DirectoryChooser(parent);
									}
								},
								SWT.NONE);
					}
				},
				SWT.NONE
		);
		location.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		ModifyListener listener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (!location.getWidgetEnabled()) {
					String path = Platform.getLocation().append(projectName.getWidget().getText()).toOSString();
					((DirectoryChooser)location.getWidget().getWidget()).setPath(path);
				}
			}
		};
		projectName.getWidget().addModifyListener(listener);
		listener.modifyText(null);
		
		Group projectSettings = new Group(composite, SWT.NONE);
		projectSettings.setText("Project settings");
		projectSettings.setLayoutData(new GridData(GridData.FILL_BOTH));
		projectSettings.setLayout(new GridLayout(1, false));
		
		final LabelledWidget<Text> author = new LabelledWidget<Text>(
			projectSettings, 
			"Author:", 
			"(This should be your splunk.com user name if you intend to publish this app to SplunkBase.)",
			new WidgetCreation<Text>() {
				public Text init(Composite parent) {
					return new Text(parent, SWT.SINGLE | SWT.BORDER);
				}
			},
			SWT.NONE
		);
		author.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final ToggledWidget<LabelledWidget<Text>> projectLabel = new ToggledWidget<LabelledWidget<Text>>(
				projectSettings, 
				"Show project in Splunk launcher", 
				false,
				true, 
				new WidgetCreation<LabelledWidget<Text>>() {
					public LabelledWidget<Text> init(Composite parent) {
						return new LabelledWidget<Text>(
							parent, 
							"Label to display:", 
							null, 
							new WidgetCreation<Text>() {
								public Text init(Composite parent) {
									return new Text(parent, SWT.SINGLE | SWT.BORDER);
								}
							}, 
							SWT.NONE
						);
					}
				},
				SWT.NONE
		);
		
		final LabelledWidget<Text> description = new LabelledWidget<Text>(
			projectSettings,
			"Description:",
			null,
			new WidgetCreation<Text>() {
				public Text init(Composite parent) {
					return new Text(parent, SWT.SINGLE | SWT.BORDER);
				}
			},
			SWT.NONE
		);
		description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Now hook up all the fields to write to options.
		ModifyListener ml;

		final Runnable appidRunnable = new Runnable() {
			@Override
			public void run() {
				options.put("appid", projectName.getWidget().getText());
				if ("".equals(options.get("appid"))) {
					pushStatus("appid", "Project name cannot be empty.");
				} else {
					popStatus("appid");
				}
			}
		};
		projectName.getWidget().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				appidRunnable.run();
			}
		});
		appidRunnable.run();
		
		final Runnable locationRunnable = new Runnable() {
			@Override
			public void run() {
				if (location.getWidgetEnabled()) {
					options.put("location", location.getWidget().getWidget().getPath());
					if (options.get("location") == null || "".equals(options.get("location"))) {
						pushStatus("location", "Location must not be empty.");
					} else {
						popStatus("location");
					}
				} else {
					options.remove("location");
					popStatus("location");
				}
			}
		};
		
		location.getWidget().getWidget().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				locationRunnable.run();
			}
		});
		location.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				locationRunnable.run();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				locationRunnable.run();
			}
		});
		locationRunnable.run();

		final Runnable authorRunnable = new Runnable() {
			@Override
			public void run() {
				options.put("author", author.getWidget().getText());
				if ("".equals(options.get("author"))) {
					pushStatus("author", "Author must not be empty.");
				} else {
					popStatus("author");
				}
			}
		};
		author.getWidget().addModifyListener(		new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				authorRunnable.run();
			}
		});
		authorRunnable.run();
		
		final Runnable labelRunnable = new Runnable() {
			@Override
			public void run() {
				if (projectLabel.getWidgetEnabled()) {
					options.put("label", projectLabel.getWidget().getWidget().getText());
					if ("".equals(options.get("label"))) {
						pushStatus("label", "Label must not be empty.");
					} else {
						popStatus("label");
					}
				} else {
					options.remove("label");
					popStatus("label");
				}
			}
		};
		projectLabel.getWidget().getWidget().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				labelRunnable.run();
			}
		});
		projectLabel.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				labelRunnable.run();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		labelRunnable.run();
		
		final Runnable descriptionRunnable = new Runnable() {
			@Override
			public void run() {
				options.put("description", description.getWidget().getText());
				if ("".equals(options.get("description"))) {
					pushStatus("description", "Description must not be empty.");
				} else {
					popStatus("description");
				}
			}
		};
		description.getWidget().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				descriptionRunnable.run();
			}
		});
		descriptionRunnable.run();
		
		setControl(composite);
				
	}
	
	public void pushStatus(String label, String message) {
		errorMessages.put(label, message);
		setMostSevereStatus();
	}
	
	public void popStatus(String label) {
		errorMessages.remove(label);
		setMostSevereStatus();
	}
	
	public void setMostSevereStatus() {
		if (errorMessages.isEmpty()) {
			setErrorMessage(null);
		} else {
			for (String message : errorMessages.values()) {
				setErrorMessage(message);
				return;
			}
		}
	}
}
