package test;

    import javax.annotation.PostConstruct;
    import java.util.EnumSet;
    import java.util.HashMap;
    import java.util.Map;

public class EnumMaps {

    private static Map<String, OwnerType> ownerTypeMap = new HashMap<>();
//    public Map<String, test.OwnerType> ownerTypeMap = new HashMap<>();

    public static Map<String, OwnerType> getOwnerTypeMap() {
        return ownerTypeMap;
    }

    public static void setMaps(){
        EnumSet.allOf(OwnerType.class).forEach(o -> {
            ownerTypeMap.put(o.getText(), o);
        });
    }

}
