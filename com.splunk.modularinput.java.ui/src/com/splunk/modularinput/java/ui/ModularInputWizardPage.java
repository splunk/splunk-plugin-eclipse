package com.splunk.modularinput.java.ui;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class ModularInputWizardPage extends WizardPage {
	private Map<String, String> options;
	private Map<String, String> errors;

	public ModularInputWizardPage(Map<String, String> options, Map<String, String> errors) {
		super("New Splunk modular input in Java");
		this.options = options;
		this.errors = errors;
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
			"Modular input name:",
			"(Name can only contain letters, numbers, '.', and '_'.)",
			new WidgetCreation<Text>() {
				public Text init(Composite parent) {
					return new Text(parent, SWT.SINGLE | SWT.BORDER);
				}
			},
			SWT.NONE
		);
		projectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group projectSettings = new Group(composite, SWT.NONE);
		projectSettings.setText("Project settings");
		projectSettings.setLayoutData(new GridData(GridData.FILL_BOTH));
		projectSettings.setLayout(new GridLayout(1, false));
		
		final LabelledWidget<Text> author = new LabelledWidget<Text>(
			projectSettings, 
			"Author:", 
			"(This should be your splunk.com user name if you intend to publish this modular input to SplunkBase.)",
			new WidgetCreation<Text>() {
				public Text init(Composite parent) {
					return new Text(parent, SWT.SINGLE | SWT.BORDER);
				}
			},
			SWT.NONE
		);
		author.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final LabelledWidget<Text> version = new LabelledWidget<Text>(
			projectSettings, 
			"Version:",
			null, 
			new WidgetCreation<Text>() {
				public Text init(Composite parent) {
					return new Text(parent, SWT.SINGLE | SWT.BORDER);
				}
			},
			SWT.NONE
		);
		version.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final ToggledWidget<LabelledWidget<Text>> projectLabel = new ToggledWidget<LabelledWidget<Text>>(
				projectSettings, 
				"Show modular input in Splunk launcher", 
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
		
		final Button workingSample = new Button(projectSettings, SWT.CHECK);
		workingSample.setSelection(true);
		workingSample.setText("Generate a working example implementation.");
		workingSample.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// Now hook up all the fields to write to options.
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
		author.getWidget().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				authorRunnable.run();
			}
		});
		authorRunnable.run();
		
		final Runnable versionRunnable = new Runnable() {
			@Override
			public void run() {
				options.put("version", version.getWidget().getText());
				if ("".equals(options.get("version"))) {
					pushStatus("version", "Version must not be empty.");
				} else {
					popStatus("version");
				}
			}
		};
		version.getWidget().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				versionRunnable.run();
			}
		});
		versionRunnable.run();
		
		final Runnable labelRunnable = new Runnable() {
			@Override
			public void run() {
				if (projectLabel.getWidgetEnabled()) {
					options.put("is_visible", "true");
					options.put("label", projectLabel.getWidget().getWidget().getText());
					if ("".equals(options.get("label"))) {
						pushStatus("label", "Label must not be empty.");
					} else {
						popStatus("label");
					}
				} else {
					options.put("is_visible", "false");
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
				String value = description.getWidget().getText();
				if ("".equals(value)) {
					options.remove("description");
				} else {
					options.put("description", value);
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
		
		final Runnable workingSampleRunnable = new Runnable() {
			@Override
			public void run() {
				if (workingSample.getSelection()) {
					options.put("working_template", "true");
				} else {
					options.remove("working_template");
				}
			}
		};
		workingSample.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				workingSampleRunnable.run();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		workingSampleRunnable.run();
		
		setControl(composite);
				
	}
	
	public void pushStatus(String label, String message) {
		errors.put(label, message);
		setMostSevereStatus();
	}
	
	public void popStatus(String label) {
		errors.remove(label);
		setMostSevereStatus();
	}
	
	public void setMostSevereStatus() {
		getWizard().getContainer().updateButtons();
		if (errors.isEmpty()) {
			setErrorMessage(null);
		} else {
			for (String message : errors.values()) {
				setErrorMessage(message);
				return;
			}
		}
	}
	
	public boolean isValid() {
		return this.errors.isEmpty();
	}
}
