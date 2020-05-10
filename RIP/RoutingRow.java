import java.util.ArrayList;
import java.util.List;

public class RoutingRow {

    private Address address;
    private List<Address> nextHop;
    private int count;

    public RoutingRow(Address address, Address nextHop, int count) {
        this.address = address;
        this.nextHop = new ArrayList<>();
        this.nextHop.add(nextHop);
        this.count = count;
    }

    public Address getAddress() {
        return address;
    }

    public List<Address> getNextHop() {
        return nextHop;
    }

    public int getCount() {
        return count;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setNextHop(List<Address> nextHop) {
        this.nextHop = nextHop;
    }

    public void setNextHop(Address nextHop) {
        this.nextHop.clear();
        this.nextHop.add(nextHop);
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return String.format("Address %s:%s, nextHope %s, count %d", address.getIp(), address.getPort(), nextHop, count);
    }

    @Override
    public int hashCode() {
        return 35*(address.hashCode() + nextHop.get(0).hashCode() + count);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RoutingRow)
            return obj.hashCode() == this.hashCode();
        return false;
    }
}