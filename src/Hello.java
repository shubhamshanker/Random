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
        System.out.println("Name = " + name);
        System.out.println("True/False : " + name.contains("\""));


    }


    public String yoyo()
    {
        String nothing = "yooy";
        return nothing;
    }

    private static void display(int hour, int minute, int second) throws InterruptedException {
//        System.out.println("Hour : " + hour + " Min : %s, Second : %d", hour, minute, second);

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
