package org.mdk.battle.mysqlagent.opencapacity;
import java.io.*;

public class PyComImpl implements OneCom {
    public String Py = null;
	@Override
	public void doAction(MetaDataManager data) {
		// TODO Auto-generated method stub
		try {
			   String wholepy = Py + " " + "\"" + data.DipObj.param + "\"";
			   System.out.println(wholepy);
	           Process pr=Runtime.getRuntime().exec(wholepy);
	            
	           BufferedReader in = new BufferedReader(new InputStreamReader(
	             pr.getInputStream()));
	           BufferedReader err = new BufferedReader(new InputStreamReader(
	  	             pr.getErrorStream()));
	           String line;
	           while ((line = err.readLine()) != null) {
		            System.out.println("err:"+ line);
		       }
	           while ((line = in.readLine()) != null) {
	            System.out.println("out:"+line);
	            data.DopObj.Update(line);
	           }
	           err.close();
	           in.close();
	           pr.waitFor();
	        } catch (Exception e) {
	               e.printStackTrace();
	        }
	        
    }

}
