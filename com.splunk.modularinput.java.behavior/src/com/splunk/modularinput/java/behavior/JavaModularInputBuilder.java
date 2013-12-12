package com.splunk.modularinput.java.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.core.builder.JavaBuilder;

public class JavaModularInputBuilder extends IncrementalProjectBuilder {
	public static final String BUILDER_ID = Activator.PLUGIN_ID + ".javaModularInputBuilder";
	
	public static void addBuilderToProject(IProject project) {
		if (!project.isOpen()) {
			return;
		}
		
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(
				new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to get description", e)
			);
			return;
		}
		
		ICommand[] commands = description.getBuildSpec();
		for (ICommand command : commands) {
			if (command.getBuilderName().equals(BUILDER_ID)) {
				return;
			}
		}
		
		ICommand newCommand = description.newCommand();
		newCommand.setBuilderName(BUILDER_ID);
		List<ICommand> newCommands = new ArrayList<ICommand>();
		for (ICommand command : commands) {
			newCommands.add(command);
		}
		newCommands.add(newCommand);

		description.setBuildSpec((ICommand[])newCommands.toArray());
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(
				new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to set description", e)
			);
		}
	}
	
	public static void removeBuilderFromProject(IProject project) {
		if (!project.isOpen()) {
			return;
		}
		
		IProjectDescription description;
		try {
			description = project.getDescription();
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(
				new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to get description", e)
			);
			return;
		}
		
		ICommand[] commands = description.getBuildSpec();
		List<ICommand> newCommands = new ArrayList<ICommand>();
		
		for (ICommand command : commands) {
			if (!command.getBuilderName().equals(BUILDER_ID)) {
				newCommands.add(command);
			}
		}
		
		description.setBuildSpec((ICommand[])newCommands.toArray());
		try {
			project.setDescription(description, null);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(
				new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to set description", e)
			);
		}
	}
	
	@Override
	protected IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		IProject project = getProject();

		@SuppressWarnings("restriction") // Yes, we really need the unexported class JavaBuilder.
		List<JavaBuilder> builders = new ArrayList<JavaBuilder>();
		
		IFolder src = project.getFolder("src");
		for (IResource resource : src.members(false)) {
			if (resource instanceof IFolder) {
				// Create a Java builder
			}
		}
		
		return null;
	}

}
