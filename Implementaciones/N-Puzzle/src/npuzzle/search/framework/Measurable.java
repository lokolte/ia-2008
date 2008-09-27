/*
 * Created on Sep 8, 2004
 *
 */
package npuzzle.search.framework;

/**
 * @author Ravi Mohan
 * 
 */
public interface Measurable {
	void setSearchMetric(String name, Object value);

	Object getSearchMetric(String name);
}