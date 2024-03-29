package aima.basic;

/**
 * @author Ravi Mohan
 * 
 */
public class Percept extends ObjectWithDynamicAttributes {
	public Percept() {

	}

	public Percept(Object key1, Object value1) {
		setAttribute(key1, value1);
	}

	public Percept(Object key1, Object value1, Object key2, Object value2) {
		setAttribute(key1, value1);
		setAttribute(key2, value2);
	}

	public Percept(Object[] keys, Object[] values) {
		assert (keys.length == values.length);

		for (int i = 0; i < keys.length; i++) {
			setAttribute(keys[i], values[i]);
		}
	}
}