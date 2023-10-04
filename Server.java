import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Server {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            // Create a server socket and bind it to a port
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started and listening on port " + PORT);

            while (true) {
                // Wait for client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Start a new thread to handle the client connection
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }finally{
            // Close the server socket
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler extends Thread{
        private Socket clientSocket;
        ObjectInputStream inputStream = null;
        ObjectOutputStream outputStream = null;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try{
                // Create input and output streams for communication
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
                outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            
                // Read data from the client (assuming it sends an Integer value)
                String studentID = (String) inputStream.readObject();
                System.out.println("Received student ID: " + studentID);
            
                // Get destinations from the client
                ArrayList<String> selectedChoices = new ArrayList<>();
                Object obj;
                while ((obj = inputStream.readObject()) != null) {
                    if (obj instanceof String) {
                        String destination = (String) obj;
                        selectedChoices.add(destination);
                        System.out.println("Received destination: " + destination);
                    }
                }
                System.out.println("All destinations received: " + selectedChoices);
            
            
                // Perform genetic algorithm
                GeneticAlgorithm ga = new GeneticAlgorithm();
                int generationCount = 0;
            
                // Initialize population
                ga.population.initializePopulation(10);
            
                // Calculate fitness of each individual
                ga.population.calculateFitness();
            
                System.out.println("Generation: " + generationCount + " Fittest: " + ga.population.fittest);
            
                // While population gets an individual with maximum fitness
                while (ga.population.fittest < 5) {
                    ++generationCount;
            
                    // Do selection
                    ga.selection();
            
                    // Do crossover
                    ga.crossover();
            
                    // Do mutation under a random probability
                    Random rn = new Random();
                    if (rn.nextInt() % 7 < 5) {
                        ga.mutation();
                    }
            
                    // Add fittest offspring to the population
                    ga.addFittestOffspring();
            
                    // Calculate new fitness value
                    ga.population.calculateFitness();
            
                    System.out.println("Generation: " + generationCount + " Fittest: " + ga.population.fittest);
                }
            
                // Get the solution from the genetic algorithm
                int solutionFitness = ga.population.getFittest().fitness;
                int[] solutionGenes = ga.population.getFittest().genes;
            
                // Prepare the response data
                List<String> selectedDestinations = new ArrayList<>();
                for (int i = 0; i < solutionGenes.length; i++) {
                    if (solutionGenes[i] == 1) {
                        selectedDestinations.add(selectedChoices.get(i));
                    }
                }
            
                // Send the result back to the client
                outputStream.writeObject(selectedDestinations);
                outputStream.flush();
            
                System.out.println("Selected destinations sent to the client.");
        
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                // Close all resources
                try {
                    if (outputStream != null)
                        outputStream.close();
                    if (inputStream != null)
                        inputStream.close();
                    if (clientSocket != null)
                        clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
