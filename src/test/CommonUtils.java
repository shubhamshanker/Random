package test;

import constants.Constants;
import org.springframework.data.util.Pair;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommonUtils {

    public static List<Pair<String, String>> getPairs(String code, String delimiter) {
        if (StringUtils.isBlank(code)) {
            return Collections.emptyList();
        }
        List<String> listNode = Arrays.asList(code.split(delimiter));
        List<Pair<String, String>> result = new ArrayList<>(0);
        IntStream.range(0, listNode.size() - 1).forEach(index ->
                result.add(Pair.of(listNode.get(index), listNode.get(index + 1))));
        return result;
    }

    public static Map<String, List<String>> getMapOfRelayToSection(String relaySections, String routeSections) {
        List<String> relaySectionList = Arrays.asList(relaySections.split(Constants.RELAY_DELIMITER));
        System.out.println(relaySectionList);
        Map<String, String> firstNodeRelayMap = relaySectionList.stream()
                .collect(Collectors.toMap(CommonUtils::getFirstFromHash, Function.identity()));

        System.out.println("First Node Relay Map :");
        System.out.println(firstNodeRelayMap);

        Map<String, String> lastNodeRelayMap = relaySectionList.stream()
                .collect(Collectors.toMap(CommonUtils::getLastFromHash, Function.identity()));

        System.out.println("Last Node Relay Map :");
        System.out.println(lastNodeRelayMap);

        Map<String, List<String>> result = new HashMap<>();
        List<Pair<String, String>> pairRouteSections = getPairs(routeSections, Constants.ROUTE_DELIMITER);

        System.out.println("Pair Route Sections :");
        System.out.println(pairRouteSections);

        boolean match = false;
        String currentRelaySection = null;
        for (Pair<String, String> originDestPair : pairRouteSections) {
            if (firstNodeRelayMap.containsKey(originDestPair.getFirst())) {
                match = true;
                currentRelaySection = firstNodeRelayMap.get(originDestPair.getFirst());
            }
            if (match) {
                System.out.println("Result 1 : ");
                System.out.println(result);
                result.putIfAbsent(currentRelaySection, new ArrayList<>(0));
                System.out.println("Result 2 : ");
                System.out.println(result);
                result.get(currentRelaySection).add(createRouteHash(originDestPair));
            }
            if (lastNodeRelayMap.containsKey(originDestPair.getSecond())) {
                match = false;
            }
        }
        return result;
    }

    public static String getFirstFromHash(String code) {
        return code.substring(0, code.indexOf(Constants.ROUTE_DELIMITER));
    }

    public static String getLastFromHash(String code) {
        return code.substring(code.indexOf(Constants.ROUTE_DELIMITER) + 1);
    }

public static String createRouteHash(Pair<String, String> originDestination) {
        return createRouteHash(originDestination.getFirst(), originDestination.getSecond());
        }

public static String createRouteHash(String originCode, String destinationCode) {
        return originCode + Constants.ROUTE_DELIMITER + destinationCode;
        }
}

//    public static String createRouteHash(Pair<String, String> originDestination) {
//        return createRouteHash(originDestination.getFirst(), originDestination.getSecond());
//    }
//
//    public static String createRouteHash(String originCode, String destinationCode) {
//        return originCode + constants.Constants.ROUTE_DELIMITER + destinationCode;
//    }
//}
