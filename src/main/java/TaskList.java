import java.util.ArrayList;

/**
 * Stores Sherlock's tasks and provides indexed access to them.
 */
class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list with an initial capacity.
     *
     * @param capacity initial number of tasks the list can hold without resizing
     */
    TaskList(int capacity) {
        tasks = new ArrayList<>(capacity);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task task to add
     */
    void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the task at the given index
     */
    Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return the removed task
     */
    Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks currently in this list.
     *
     * @return current task count
     */
    int size() {
        return tasks.size();
    }
}
