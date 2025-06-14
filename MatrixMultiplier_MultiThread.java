/*  tugce yesilyurt-21COMP1021
 * humeyra bilgin-21COMP1004
 */

 
package opsys_project;


import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class MatrixMultiplier_MultiThread {
    private static int MATRIX_SIZE;
    private static int NUM_THREADS;
    private static long duration;
    private static int[][] finalResult;
    private static int[][] matrixB; // B matrisi, belleðe bir kez yüklenir ve tüm threadler kullanýlýr 
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        
        System.out.print("Enter the matrix input file path: ");
        String filePath = scanner.nextLine();
        
        
        System.out.print("Enter the matrix size (e.g., 1000 for 1000x1000): ");
        MATRIX_SIZE = scanner.nextInt();
        
        System.out.print("Enter the number of threads: ");
        NUM_THREADS = scanner.nextInt();

        
        finalResult = new int[MATRIX_SIZE][MATRIX_SIZE];
         // B matrisini dosyadan oku
        loadMatrixB(filePath);

         // Her threade kaç satýr düþeceði
        int rowsPerThread = MATRIX_SIZE / NUM_THREADS;
        int remainingRows = MATRIX_SIZE % NUM_THREADS;
        int start = 0;

        
        Thread[] threads = new Thread[NUM_THREADS];

         // Her thread için baþlangýç ve bitiþ satýrý ata        
        for (int k = 0; k < NUM_THREADS; k++) {
            int extraRow = 0;
            if (k < remainingRows) {
                extraRow = 1;
            }
            int end = start + rowsPerThread + extraRow;
            
              // Worker sýnýfýndan oluþturulan thread 
            Worker worker = new Worker(start, end, filePath);
            threads[k] = new Thread(worker); 
            start = end;// Sonraki thread için baþlangýç satýrý güncellenir
            
        }


        long startTime = System.currentTimeMillis();
        

        // Tüm threadler baþlatýlýr
        for (int k = 0; k < NUM_THREADS; k++) {
            threads[k].start();
        }

             // Tüm threadlerin bitmesi beklenir
        for (int k = 0; k < NUM_THREADS; k++) {
            threads[k].join();
        }
        
        

        long endTime = System.currentTimeMillis();
        duration = endTime - startTime;

        
        System.out.println("Matrix multiplication completed.");
        System.out.println("Time taken (multithreaded): " + duration + " ms");
        
        scanner.close();
    }
      
       // Matrix B'yi dosyadan okuyup belleðe yükleyen metod
    public static void loadMatrixB(String filePath) throws IOException {

        matrixB = new int[MATRIX_SIZE][MATRIX_SIZE];

        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean matrixBFound = false;
            int rowCount = 0;

            while ((line = fileReader.readLine()) != null) {
                line = line.trim();
                 
                   // Matrix B etiketi bulunduktan sonra okumaya baþlanýr
                if (line.startsWith("Matrix B")) {
                    matrixBFound = true;
                    continue;
                }

                if (!matrixBFound) continue;

                if (!line.isEmpty() && rowCount < MATRIX_SIZE) {
                    String[] nums = line.split("\\s+");
                    for (int col = 0; col < MATRIX_SIZE && col < nums.length; col++) {
                        matrixB[rowCount][col] = Integer.parseInt(nums[col]);
                    }
                    rowCount++;
                    if (rowCount >= MATRIX_SIZE) break;
                }
            }
        }
    }
    

    static class Worker implements Runnable {
        
        private int start, end; // workerin iþlem yapacaðý satýr aralýðý
        private String filePath;  // Matris A'nýn bulunduðu dosya yolu

        public Worker(int start, int end, String filePath) {
            this.start = start;
            this.end = end;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {

                int[][] threadRows = readMatrixARows(filePath, start, end);
                

                for (int row = 0; row < threadRows.length; row++) {
                    for (int col = 0; col < MATRIX_SIZE; col++) {
                        int sum = 0;
                        for (int i = 0; i < MATRIX_SIZE; i++) {
                            sum += threadRows[row][i] * matrixB[i][col];
                        }
                        finalResult[start + row][col] = sum;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
             // Matrix A'nýn sadece bu thread'e ait satýrlarýný dosyadan okuyan metod
        public int[][] readMatrixARows(String filePath, int start, int end) throws IOException {
            // Read rows of matrix A here
            int numRows = end - start;
            int[][] threadRows = new int[numRows][MATRIX_SIZE];
            
            try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean matrixAFound = false;
                int currentRow = 0; // Tüm dosya içindeki satýr sayacý
                int rowCounter = 0;// threadin okuduðu satýr sayýsý                
                while ((line = fileReader.readLine()) != null && rowCounter < numRows) {
                    if (line.startsWith("Matrix A")) {
                        matrixAFound = true;
                        continue;
                    }
                    
                    if (matrixAFound && !line.trim().isEmpty() && !line.startsWith("Matrix B")) {
                        if (currentRow >= start && currentRow < end) {
                            String[] nums = line.trim().split("\\s+");
                            for (int col = 0; col < MATRIX_SIZE && col < nums.length; col++) {
                                threadRows[rowCounter][col] = Integer.parseInt(nums[col]);
                            }
                            rowCounter++;
                        }
                        currentRow++;
                    }
                }
            }
            return threadRows;
        }
    }
} 