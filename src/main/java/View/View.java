package View;

import java.util.Scanner;

public class View {
    private Scanner scanner = new Scanner(System.in);

    public int getIntegerInput() {
        while (!scanner.hasNextInt()) {
            showMessage("Wrong input.");
            scanner.nextLine();
        }
        int num = scanner.nextInt();
        scanner.nextLine();
        return num;
    }
    public void showMessage(String message){
        System.out.println(message);
    }
    public String getStringInput(){
        return scanner.nextLine();
    }
}
