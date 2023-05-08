package fr.ul.miage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Plan p = new Plan();
//    	ArrayList<Line> l = p.getLines();
//    	for (int i = 0; i < l.size(); i++) {
//			l.toString();
//		}
    	//System.out.println(p.toString());
//    	HashMap<String, ArrayList<Station>> map = p.reachableStations();
//    	for (String cle : map.keySet()) {
//    		ArrayList<Station> valeur = map.get(cle);
//    	    System.out.println(cle + " : " + valeur);
//    	}
    	
    	
    	for (int i = 0; i < p.getArcs().size(); i++) {
			System.out.println(p.getArcs().get(i).getDistance() + "  "+p.getArcs().get(i).getStartStation().getName() + "  "+p.getArcs().get(i).getEndStation().getName() + "  ");
		}
			
       	ArrayList<String> paths = p.starA("T", "V");
    	for(String s :paths ) {
    		System.out.println(s+"\n");
    	}
			
		
    	
    }
}
