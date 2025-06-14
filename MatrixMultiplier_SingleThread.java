/*  tugce yesilyurt-21COMP1021
 * humeyra bilgin-21COMP1004
 */
package opsys_project;

import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader; //Dosyay� sat�r sat�r okumak i�in tamponlu okuyucu
import java.io.FileReader;

public class MatrixMultiplier_SingleThread {
    private static int MATRIX_SIZE;
    private static long duration; //matrix �al��ma s�resi
    private static int[][] matrixA;
    private static int[][] matrixB;
    private static int[][] finalResult;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the matrix input file path: ");
        String filePath = scanner.nextLine();

        System.out.print("Enter the matrix size (e.g., 1000 for 1000x1000): ");
        MATRIX_SIZE = scanner.nextInt();

        matrixA = new int[MATRIX_SIZE][MATRIX_SIZE];
        matrixB = new int[MATRIX_SIZE][MATRIX_SIZE];
        finalResult = new int[MATRIX_SIZE][MATRIX_SIZE];

        loadMatrixA(filePath); //dosyadan matrisleri oku
        loadMatrixB(filePath);

        long startTime = System.currentTimeMillis();//matrix �arp�m zaman�



//single thread matrix �arp�m�
        for (int row = 0; row < MATRIX_SIZE; row++) {
            for (int col = 0; col < MATRIX_SIZE; col++) {
                int sum = 0;
                for (int i = 0; i < MATRIX_SIZE; i++) {

                       //A n�n sat�r� ve B nin s�tunu �arp topla
                    sum += matrixA[row][i] * matrixB[i][col];
                }
                finalResult[row][col] = sum;
            }
        }
        long endTime = System.currentTimeMillis(); //i�lem s�resi
        duration = endTime - startTime;

        System.out.println("Matrix multiplication completed (single-threaded).");
        System.out.println("Time taken (single-threaded): " + duration + " ms");

        scanner.close();
    }


       // matrix A'y� dosyadan sat�r sat�r okuyup doldurur


    public static void loadMatrixA(String filePath) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean matrixAFound = false;
            int row = 0;


                // Dosya sat�r sat�r okunuyor
            while ((line = fileReader.readLine()) != null && row < MATRIX_SIZE) {
                line = line.trim();
                if (!matrixAFound) {
                    if (line.startsWith("Matrix A")) {    //"Matrix A" g�r�lene kadar devam et
                        matrixAFound = true;
                    }
                    continue;
                }
                
                // "Matrix A" sat�r�ndan sonra bo� olmayan sat�rlar okunuyor
                if (!line.isEmpty()) {
                    String[] nums = line.split("\\s+");
                    for (int col = 0; col < MATRIX_SIZE && col < nums.length; col++) {
                        matrixA[row][col] = Integer.parseInt(nums[col]);
                    }
                    row++;
                }
            }
        }
    }


           // Matrix B'yi dosyadan okuyup belle�e alan metot

    public static void loadMatrixB(String filePath) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int row = 0;
            boolean matrixBFound = false;

            while ((line = fileReader.readLine()) != null) {
                line = line.trim();

                if (!matrixBFound) {  // "Matrix B" bulunana kadar devam et
                    if (line.startsWith("Matrix B")) {
                        matrixBFound = true;
                    }
                    continue;
                }

                if (!line.isEmpty() && row < MATRIX_SIZE) {
                    String[] nums = line.split("\\s+");
                    for (int col = 0; col < MATRIX_SIZE && col < nums.length; col++) {
                        matrixB[row][col] = Integer.parseInt(nums[col]);
                    }
                    row++;
                }
            }
        }
    }
}