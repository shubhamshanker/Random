import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class Hello {
    private static final String ROUTE_DELIMITER = ",";
    public static void main (String[] args) throws InterruptedException {

        Map<String, Integer> newmap = new HashMap<>();
        newmap.put("jejej", 1);
        System.out.println(newmap.get("asdfasdf"));
        System.out.println("Hi Guys");
        LocalTime thisSec;

        for (;;) {
            thisSec = LocalTime.now();

            // implementation of display code is left to the reader
            display(thisSec.getHour(), thisSec.getMinute(), thisSec.getSecond());
        }


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
