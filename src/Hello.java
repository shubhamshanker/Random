import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Hello {
    private static final String ROUTE_DELIMITER = ",";
    public static void main (String[] args) throws InterruptedException {

        String name;
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter name : ");
        name = sc.nextLine();
        System.out.println("True/False : " + name.contains("\""));

    }

    private static void display(int hour, int minute, int second) throws InterruptedException {

        try {
            System.out.println(hour + "\n" + minute + "\n" + second);
            System.out.format("Hour : %s", hour);
            Thread.sleep(1000);
        }
        catch (InterruptedException ex){
            System.out.format("Thread inturuptteds : %s", ex);
        }

    }
}
