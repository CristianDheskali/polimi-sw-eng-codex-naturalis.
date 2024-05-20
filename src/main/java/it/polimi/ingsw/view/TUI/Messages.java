package it.polimi.ingsw.view.TUI;

public class Messages {

    private static Messages instance;

    private Messages() {}

    public static Messages getInstance() {
        if (instance == null) instance = new Messages();
        return instance;
    }

    public void info(String message) {
        System.out.println(TextColor.BRIGHT_BLUE + "[INFO] " + TextColor.RESET + message);
    }

    public void error(String message) {
        System.out.println(TextColor.RED + "[ERROR] " + TextColor.RESET + message);
    }

    public void input(String message) {
        System.out.print(TextColor.BRIGHT_YELLOW + "[INPUT] " + TextColor.RESET + message);
    }

    public void message(String message, boolean isPrivate) {
        if (isPrivate) {
            System.out.println(TextColor.BRIGHT_PURPLE + "[PRIVATE MESSAGE]: " + TextColor.RESET + message);
        } else {
            System.out.println(TextColor.BRIGHT_PURPLE + "[MESSAGE]: " + TextColor.RESET + message);
        }
    }

    public String getInfoMessage(String message) {
        return TextColor.BRIGHT_BLUE + "[INFO] " + TextColor.RESET + message;
    }

    public String getErrorMessage(String message) {
        return TextColor.RED + "[ERROR] " + TextColor.RESET + message;
    }

    public String getInputMessage(String message) {
        return TextColor.BRIGHT_YELLOW + "[INPUT] " + TextColor.RESET + message;
    }

    public String getMessage(String message, boolean isPrivate) {
        if (isPrivate) {
            return TextColor.BRIGHT_PURPLE + "[PRIVATE MESSAGE] " + TextColor.RESET + message;
        } else {
            return TextColor.BRIGHT_PURPLE + "[MESSAGE] " + TextColor.RESET + message;
        }
    }

}
