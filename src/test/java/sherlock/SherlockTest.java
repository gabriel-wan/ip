package sherlock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the response API shared by Sherlock's text and graphical interfaces. */
class SherlockTest {
    @TempDir
    private Path temporaryFolder;

    @Test
    void getResponse_addThenList_returnsUpdatedTaskList() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        assertEquals("added: [T][ ] inspect footprint", sherlock.getResponse("todo inspect footprint"));
        assertTrue(sherlock.getResponse("list").contains("1. [T][ ] inspect footprint"));
    }

    @Test
    void getResponse_bye_returnsFarewell() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        assertEquals("Bye. Hope to see you again soon!", sherlock.getResponse("bye"));
    }
}
