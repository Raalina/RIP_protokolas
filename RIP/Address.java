public class Address {

    private String ip;
    private int port;

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", ip, port);
    }

    @Override
    public int hashCode() {
        return 31*ip.hashCode()+port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Address)
            return this.hashCode() == ((Address) obj).hashCode();
        return false;
    }
}
