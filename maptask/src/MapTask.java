
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Resource;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class MapTask extends Task implements TaskContainer {
    private String propertyName;
    private DirSet dirSet;
    private List<Task> tasks = new ArrayList<Task>();

    public void setPropertyName(String propertyName) {
	this.propertyName = propertyName;
    }

    private class IterableWrapper<T> implements Iterable<T> {
	Iterator<T> it;

	public IterableWrapper(Iterator<T> it) {
	    this.it = it;
	}

	public Iterator<T> iterator() {
	    return this.it;
	}
    }

    public String getPropertyName() {
	return this.propertyName;
    }
    public void execute() throws BuildException {
	String oldPropertyValue = project.getProperty(this.propertyName);

	try {
		IterableWrapper<Resource> it = new IterableWrapper<Resource>(dirSet.iterator());
	    for (Resource f : it) {
	    	project.setProperty(this.propertyName, f.getName());
		    
	    	for (Task t : tasks) {
	    		t.perform();
	    	}
	    }
	} finally {
	    project.setProperty(this.propertyName, oldPropertyValue);
	}
    }

    public void addConfiguredDirSet(DirSet dirSet) {
    	this.dirSet = dirSet;
    }

    public void addTask(Task task) {
    	this.tasks.add(task);
    }
}
