import java.util.*;

public class RoutingTable {

    private Vector<RoutingRow> rows;
    private HashMap<Address, RoutingRow> mapRows;

    public RoutingTable() {
        rows = new Vector<>();
        mapRows = new HashMap<>();
    }

    public List<Address> getNextHop(Address address) {
        return mapRows.get(address) != null
                ? mapRows.get(address).getNextHop()
                : new ArrayList<>();
    }

    public Vector<RoutingRow> getRoutingRows() {
        return rows;
    }

    public Optional<RoutingRow> findRoutingRow(Address address) {
        return Optional.ofNullable(mapRows.get(address));
    }

    public void add(Address address, RoutingRow row) {
        rows.add(row);
        mapRows.put(address, row);
    }

    public void update(Address address, RoutingRow oldRow, RoutingRow newRow) {
        rows.set(rows.indexOf(oldRow), newRow);
        mapRows.put(address, newRow);
    }

    public void update(Address address, int index, RoutingRow newRow) {
        rows.set(index, newRow);
        mapRows.put(address, newRow);
    }

    public void remove(RoutingRow row) {
        rows.remove(row);
        mapRows.remove(row);
    }

    public Optional<Router> getNextHopRouter(List<Router> neightbours, Address address) {
        return neightbours
                .stream()
                .filter(n -> n.getAddress().equals(getNextHop(address).size() >= 1 ? getNextHop(address).get(0) : ""))
                .findFirst();
    }
}
