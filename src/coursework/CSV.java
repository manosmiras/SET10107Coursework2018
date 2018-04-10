package coursework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class CSV {
	public PrintWriter pw; //= new PrintWriter(new File("test.csv"));
    public StringBuilder sb;// = new StringBuilder();
	
    public CSV()
    {
    	sb = new StringBuilder();
    }
    
    public void append(String data)
    {
    	sb.append(data);
    }
    
	public void save(String name) throws FileNotFoundException
	{
    	pw = new PrintWriter(new File(name + ".csv"));
        pw.write(sb.toString());
        pw.close();
        System.out.println("CSV saved.");
	}
}
