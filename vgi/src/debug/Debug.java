package debug;

/**
 *  Debug.java for vgi in /home/loulou/lrde_proj/vgi/src
 * Made by Louis-Noel Pouchet
 * Login   <loulou@lrde.epita.fr>
 * Started on  Wed Mar  2 16:13:49 2005 Louis-Noel Pouchet
 * Last update Wed Mar  2 16:13:54 2005 Louis-Noel Pouchet
 */

import java.util.ArrayList;
import java.util.Vector;


public class Debug
{
    public static void echo(double obj)
    {
	System.out.println(obj);
    }

    public static void echo(boolean b)
    {
	System.out.println(b);
    }

    public static void echo(Object obj)
    {
	System.out.println(obj);
    }

    public static void echo(Object[] obj)
    {
	for (int i = 0; i < obj.length; ++i)
	    System.out.println(obj[i]);
    }

    public static void echo(Vector obj)
    {
	for (int i = 0; i < obj.size(); ++i)
	    System.out.println(obj.get(i));
    }

    public static void echo(ArrayList obj)
    {
	for (int i = 0; i < obj.size(); ++i)
	    System.out.println(obj.get(i));
    }
}
