import java.net.SocketException;
import java.util.Scanner;

public class Main {

    private static final String LOCALHOST = "127.0.0.1";

    public static void main(String[] args) throws SocketException, InterruptedException {
            Network network = new Network();

            Router n1 = new Router(new Address(LOCALHOST, 100));
            Router n2 = new Router(new Address(LOCALHOST, 200));
            Router n3 = new Router(new Address(LOCALHOST, 300));
            Router n4 = new Router(new Address(LOCALHOST, 400));
            Router n5 = new Router(new Address(LOCALHOST, 600));
            Router n7 = new Router(new Address(LOCALHOST, 700));

            network.addNewRouter(n1);
            network.addNewRouter(n2, n1);
            network.addNewRouter(n3, n2);
            network.addNewRouter(n4, n2);
            network.addRouter(n3, n4);
            network.addNewRouter(n5, n4);
            network.addNewRouter(n7, n3);
            network.addRouter(n7, n5);
			
            startWorking(network, n3, n7);

            while (true) {
                try {
                    printMenu();

                    Scanner scanner = new Scanner(System.in);
                    int choice = scanner.nextInt();

                    String firstIp;
                    int firstPort;
                    String secondIp;
                    int secondPort;
                   
                    switch (choice) {
                        case 1:
                            network.showRouters();
                            break;
                        case 2:
                            System.out.println("Enter new router ip: ");
                            firstIp = scanner.next();
                            System.out.println("Enter new router port: ");
                            firstPort = scanner.nextInt();
                            System.out.println("Enter first neighbour ip: ");
                            secondIp = scanner.next();
                            System.out.println("Enter first neighbour port: ");
                            secondPort = scanner.nextInt();

                            network.addNewRouter(new Router(new Address(firstIp, firstPort)),
                                    new Address(secondIp, secondPort));
                            break;
                        case 3:
                            System.out.println("Enter first router ip: ");
                            firstIp = scanner.next();
                            System.out.println("Enter first router port: ");
                            firstPort = scanner.nextInt();
                            System.out.println("Enter second router ip: ");
                            secondIp = scanner.next();
                            System.out.println("Enter second router port: ");
                            secondPort = scanner.nextInt();
                            network.linkRouters(new Address(firstIp, firstPort),
                                    new Address(secondIp, secondPort));
                            break;
                        case 4:
                            System.out.println("Enter router ip: ");
                            firstIp = scanner.next();
                            System.out.println("Enter router port: ");
                            firstPort = scanner.nextInt();
                            network.removeRouter(new Address(firstIp, firstPort));
                            break;
                        case 6:
                            System.out.println("Enter Router ip: ");
                            firstIp = scanner.next();
                            System.out.println("Enter Router port: ");
                            firstPort = scanner.nextInt();
                            System.out.println("Enter destination ip: ");
                            secondIp = scanner.next();
                            System.out.println("Enter destination port: ");
                            secondPort = scanner.nextInt();
                            new Thread(() -> network.getRouter(new Address(firstIp, firstPort))
                                    .send(new Message(new Address(secondIp, secondPort), "packet"))).start();
                            break;
                        case 7:
                            network.showRoutingTables();
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                }
            }
    }

    static void printMenu() {
        System.out.println("Commands:");
        System.out.println("1. View routers");
        System.out.println("2. Add router");
        System.out.println("3. Add link");
        System.out.println("4. Delete router");
		System.out.println("5. Delete link");
        System.out.println("6. Send packet");
        System.out.println("7. Show tables");
    }

    static void startWorking(Network network, Router removalRouter, Router destination) throws InterruptedException {
        Thread.sleep(18000);

        new Thread(() -> network.getRouter(network.getInitialRouter().getAddress())
                .send(new Message(destination.getAddress(), "packet"))).start();
        Network.removeRouter(removalRouter.getAddress());

        Thread.sleep(18000);
    }
}