/**
 * The entry point for Sherlock, a detective-themed personal assistant chatbot.
 */
public class Sherlock {
    /**
     * Starts Sherlock, prints a greeting, and then exits.
     *
     * @param args command-line arguments, which are not used at this level
     */
    public static void main(String[] args) {
        String banner = "  ____  _               _            _    \n"
                + " / ___|| |__   ___ _ __| | ___   ___| | __\n"
                + " \\___ \\| '_ \\ / _ \\ '__| |/ _ \\ / __| |/ /\n"
                + "  ___) | | | |  __/ |  | | (_) | (__|   < \n"
                + " |____/|_| |_|\\___|_|  |_|\\___/ \\___|_|\\_\\\n";

        System.out.println(banner);
        System.out.println("Hello! I'm Sherlock, your detective assistant.");
        System.out.println("What can I do for you?");
        System.out.println("Bye. Hope to see you again soon!");
    }
}
