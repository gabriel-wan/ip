package sherlock.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import sherlock.command.Command;
import sherlock.exception.SherlockException;

/** Tests conversion of user input into Sherlock commands. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTodo_createsAddCommandWithTask() throws SherlockException {
        Command command = parser.parse("todo review clues", 0);

        assertEquals(Command.Type.ADD, command.getType());
        assertEquals("[T][ ] review clues", command.getTask().toString());
    }

    @Test
    void parseMark_usesOneBasedTaskNumber() throws SherlockException {
        Command command = parser.parse("mark 2", 2);

        assertEquals(Command.Type.MARK, command.getType());
        assertEquals(2, command.getTaskNumber());
    }

    @Test
    void parseTodoWithoutDescription_throwsHelpfulException() {
        SherlockException exception = assertThrows(SherlockException.class,
                () -> parser.parse("todo", 0));

        assertEquals("I need a case description before I can add it.", exception.getMessage());
    }
}
