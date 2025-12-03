package game;
import java.io.File;                 
import java.io.FileNotFoundException; 
import java.util.Scanner;            

public class SudokuBoard {

    // 3mla array aknha matrix w 7tet feha el size
    private final int[][] grid = new int[9][9];

    // constructor b load el data meen file lel matrix 3ala tol
    public SudokuBoard(String filePath) {
        loadFromCSV(filePath);
    }

   
    private void loadFromCSV(String filePath) {
        
        try (Scanner scanner = new Scanner(new File(filePath))) {

            for (int row = 0; row < 9; row++) {
                
                
                if (scanner.hasNextLine()) {
                  
                    String line = scanner.nextLine(); 

                  
                    String[] numbers = line.split(",");

                    
                    for (int col = 0; col < 9; col++) {
                        // The file has Strings ("5"), but we need Integers (5).
                        // Integer.parseInt() does this conversion.
                        // .trim() removes any accidental spaces (like " 5").
                        grid[row][col] = Integer.parseInt(numbers[col].trim());
                    }
                }
            }
        } catch (FileNotFoundException e) {
           
            System.err.println("Error: The file " + filePath + " was not found.");
            System.exit(1); // 1 means "completed with error"
        }
    }

 
    //  getter tgb row w colum 
    public int getCell(int row, int col) {
        return grid[row][col];
    }
    
   //mlhash lzma ela lw 3ozt a8yr size w ab2a 3rfo
   // public int getSize() {
      //  return 9;
    //}
}