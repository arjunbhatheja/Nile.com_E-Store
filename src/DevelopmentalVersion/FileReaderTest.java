
package DevelopmentalVersion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;

public class FileReaderTest {
	private static void readInventoryFile() {
		BufferedReader inputBufReader = null;
		String inventoryLine;
		try {
			FileReader inputFileReader = new FileReader("inventory.csv");
			inputBufReader = new BufferedReader(inputFileReader);
			inventoryLine = inputBufReader.readLine(); // read from file
			while (inventoryLine != null) {
				System.out.println(inventoryLine);
				inventoryLine = inputBufReader.readLine(); // read next line from file
			}
		} catch (FileNotFoundException fileNotFoundException) {
			JOptionPane.showMessageDialog(null, "Error: File not found", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ioException) {
			JOptionPane.showMessageDialog(null, "Error: Problem reading from file", "Nile Dot Com - ERROR", JOptionPane.ERROR_MESSAGE);
		} finally {
			try {
				if (inputBufReader != null) {
					inputBufReader.close();
				}
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	public static void main(String[] args) {
		readInventoryFile();
	}
}
