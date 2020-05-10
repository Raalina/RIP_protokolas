import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Router implements Runnable {

    private HashMap<Address, Long> times = new HashMap<>();
	public boolean visited;
    public Address address;
    public List<Router> neightbours = new ArrayList<>();
    public RoutingTable table;

    public Router(Address address, List<Router> neightbours, RoutingTable table) throws SocketException {
        this.address = address;
        this.neightbours = neightbours;
        this.table = table;
    }

    public Router(Address address) throws SocketException {
        this.address = address;
        this.neightbours = new ArrayList<>();
        this.table = new RoutingTable();
    }

    @Override
    public void run() {
        new Thread(this::updateRoutingTable).start();
        new Thread(this::markOrRemoveRouter).start();
    }

    private void updateRoutingTable() {
        while (true) {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mergeRoutingTablesOfNeightbours();
            for (Router router : getNeightbours()) {
                times.put(router.getAddress(), System.currentTimeMillis());
                request(router);
            }
        }
    }

    private void mergeRoutingTablesOfNeightbours() {
        if (this.table.getRoutingRows().stream().noneMatch(r -> r.getAddress().equals(this.getAddress()))) {
            table.add(this.getAddress(), new RoutingRow(this.getAddress(), this.getAddress(), 0));
        }
        for (Router router : getNeightbours()) {
            if (this.table.getRoutingRows().stream().noneMatch(r -> r.getAddress().equals(router.getAddress()))) {
                table.add(router.getAddress(), new RoutingRow(router.getAddress(), router.getAddress(), 1));
            }
        }
    }

    private void markOrRemoveRouter() {
        while (true) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Router router : getNeightbours()) {
                if (!router.address.equals(this.address) && router.getAddress() != null && times.get(router.getAddress()) != null) {
                 if (System.currentTimeMillis() - times.get(router.getAddress()) >= 180000) {
                     Network.removeRoutingRows(router.getAddress());
                 }
                 if (System.currentTimeMillis() - times.get(router.getAddress()) >= 240000) {
                     Network.removeRouter(router.getAddress());
                     times.remove(router.getAddress());
                 }
                }
            }
        }
    }

    
    public void send(Message message) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Optional<Router> details = table.getNextHopRouter(neightbours, message.getDestAddr());
        if (details.isPresent()) {
            table.findRoutingRow(message.getDestAddr())
                    .ifPresent(x -> printMessageSending(message, x.getNextHop().get(0)));
            if (address.equals(details.get().getAddress()))
                receive(message);
            else
                details.get().receive(message);
        } else {
            System.out.println("Failed to send message from " + address.toString() + " to " + message.getDestAddr().toString());
        }

    }

    public void receive(Message message) {
        if (message.getDestAddr().equals(address)) {
            printReceivedMessage(message);
        } else {
            send(message);
        }
    }

    private void printMessageSending(Message message, Address nextHop) {
        System.out.println(String.format("Sending message from router %s to %s", address.toString(), nextHop.toString()));
    }

    private void printReceivedMessage(Message message) {
        System.out.println(String.format("Delivered message to router %s", address.toString()));
    }

    public void request(Router router) {
        if (isReachableRouter(router))
            router.response(this);
    }
	
	public Address getAddress() {
        return address;
    }

    public List<Router> getNeightbours() {
        return neightbours;
    }

    public void removeNeightbour(Router details) {
        neightbours.remove(details);
    }

    public void addNeightbour(Router neightbour) {
        neightbours.add(neightbour);
    }

    public void printLink(Router router) {
        System.out.println(String.format("%s-%s", address, router.getAddress()));
    }

    public RoutingTable getTable() {
        return table;
    }

    public void response(Router router) {
        if (isReachableRouter(router)) {
            router.receiveTable(this.getAddress(), this.table);
        }
    }

    private synchronized boolean isReachableRouter(Router router) {
        return this.table.getRoutingRows()
                .stream()
                .noneMatch(t -> t.getAddress()
                        .equals(router.getAddress())
                        && t.getCount() >= 16); //pagal RIP protokola jei daugiau 15 - nebepasiekiama
    }

    public void receiveTable(Address routerAddress, RoutingTable table) {
        mergeTable(routerAddress, table);
    }

    private synchronized void mergeTable(Address routerAddress, RoutingTable table) {
        for (int i = 0; i < table.getRoutingRows().size(); i++) {
            final RoutingRow r1 = table.getRoutingRows().get(i);
            if (this.table.getRoutingRows().stream().noneMatch(r -> r.getAddress().equals(r1.getAddress()))) {
                this.table.add(r1.getAddress(), new RoutingRow(r1.getAddress(), routerAddress, r1.getCount()+1));
            } else {
                for (RoutingRow r2 : this.table.getRoutingRows()) {
                    if (r1.getAddress().equals(r2.getAddress()) && r1.getCount()+1 < r2.getCount()) {
                        this.table.update(r1.getAddress(), r2, new RoutingRow(r1.getAddress(), routerAddress, r1.getCount()+1));
                    }
                }
            }
        }
    }
}