package storage;

/**
 * LabelFactory.java for Vcsn in /home/loulou/enst/VcsnInterface
 * Made by Loulou Pouchet
 * Login   <loulou@lrde.epita.fr>
 * Started on  Wed Dec 15 16:26:13 2004 Loulou Pouchet
 * Last update Sat Jun 18 23:19:41 2005 Louis-Noel Pouchet
 * package vaucanson.VJToolKit;
 */

import java.util.Vector;

public class LabelFactory
{
    public LabelFactory()
    {
	labels_ = new Vector();
	prefix_ = "s";
    }

    public LabelFactory(String prefix)
    {
	labels_ = new Vector(); 	
	prefix_ = prefix;
    }


    // Deal with labels for new states.
    /*
    public void		init_label_factory(VJGraph graph)
    {
	labels_.removeAllElements();
	State[] st = VJGraph.get_states(graph);
	for (int i = 0; i < st.length; ++i)
		labels_.add(((State) st[i]).name_get());
    }
    */

    public void		update_label_factory(String name)
    {
	if (! labels_.contains(name))
	    labels_.add(name);
    }

    public void		delete_element(String name)
    {
	labels_.remove(name);
    }
    

    public String	get_new_label()
    {
	int		count = 0;
	String		name = prefix_ + Integer.toString(count);
	
	while (labels_.contains(name))
	    name = prefix_ + Integer.toString(++count);
	update_label_factory(name);
	return name;
    }
    
    
    //By Mysterious.
    public String	get_label()
    {
	int		count = 0;
	String		name = prefix_ + Integer.toString(count);
	
	while (labels_.contains(name))
	    name = prefix_ + Integer.toString(++count);
	return name;
    }


    protected Vector	labels_;
    protected String	prefix_;
}
