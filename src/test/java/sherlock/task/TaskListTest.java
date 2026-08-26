package sherlock.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests task list storage and retrieval behavior. */
class TaskListTest {
    @Test
    void addThenGet_preservesTaskAndIncreasesSize() {
        TaskList tasks = new TaskList(1);
        Task task = new Todo("review clues");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void delete_removesTaskAndReturnsIt() {
        TaskList tasks = new TaskList(2);
        Task firstTask = new Todo("first clue");
        Task secondTask = new Todo("second clue");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(0);

        assertEquals(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertEquals(secondTask, tasks.get(0));
    }
}
