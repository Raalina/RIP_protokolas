import java.net.SocketException;
import java.util.*;

public class Network {

    private static final String LOCALHOST = "127.0.0.1";

    private static Router initialRouter;

    static {
        try {
            initialRouter = new Router(new Address(LOCALHOST, 64));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<Address, Thread> executions = new HashMap<>();
    private static HashMap<Address, Router> routers = new HashMap<>();

    public Network() throws SocketException {
        routers.put(initialRouter.getAddress(), initialRouter);
        new Thread(initialRouter).start();
    }

    public void addRouter(Router one, Router two) {
        two.addNeightbour((Router)one);
        one.addNeightbour((Router)two);

    }

    public void addNewRouter(Router router) throws SocketException {
        addNewRouter(router, initialRouter);
    }

    public void addNewRouter(Router one, Router two) {
        routers.put(one.getAddress(), one);
        addRouter(one, two);
        final Thread thread = new Thread(one);
        executions.put(one.getAddress(), thread);
        thread.start();
    }

    public void addNewRouter(Router one, Address two) {
        routers.put(one.getAddress(), one);
        addRouter(one, routers.get(two));
        final Thread thread = new Thread(one);
        executions.put(one.getAddress(), thread);
        thread.start();
    }

    public static void removeRouter(Address address) {
        if (address.getIp().equals(initialRouter.getAddress().getIp()) &&
                address.getPort() == initialRouter.getAddress().getPort()) {
            System.out.println("Initial router cannot be removed");
            return;
        }
        final Router router = routers.get(address);
        final List<Router> details = router.getNeightbours();
        for (int i = details.size()-1; i >= 0; i--) {
            details.get(i).removeNeightbour(router);
            router.removeNeightbour(details.get(i));
        }
        removeRoutingRows(address);
        executions.remove(address);
    }

    public void showRouters() {
        HashMap<String, Boolean> isPair = new HashMap<>();

        for (Router n1 : routers.values()) {
            for (Router n2 : n1.getNeightbours()) {
                final String pair = n1.getAddress().toString()+n2.getAddress().toString();
                final String reversePair = n2.getAddress().toString()+n1.getAddress().toString();
                if (isPair.get(pair) == null || !isPair.get(pair)) {
                    isPair.put(pair, true);
                    isPair.put(reversePair, true);
                    n1.printLink(n2);
                }
            }
        }
    }

    public static void removeRoutingRows(Address address) {
        for (Router router : Network.getRouters().values()) {
            for (int i = router.getTable().getRoutingRows().size()-1; i >= 0; i--) {
                RoutingRow row = router.getTable().getRoutingRows().get(i);
                if (row.getAddress().equals(address) || row.getNextHop().get(0).equals(address)) {
                    router.getTable().getRoutingRows().remove(row);
                }
            }
        }
    }

    public void showRoutingTables() {
        for (Router router : routers.values()) {
            System.out.printf("From router %s\n", router.getAddress());
            router.getTable().getRoutingRows().forEach(System.out::println);
        }
    }

    public void linkRouters(Address first, Address second) {
        addRouter(routers.get(first), routers.get(second));
    }

    public Router getRouter(Address address) {
        return routers.get(address);
    }

    public Router getInitialRouter() {
        return initialRouter;
    }

    public static HashMap<Address, Router> getRouters() {
        return routers;
    }
}