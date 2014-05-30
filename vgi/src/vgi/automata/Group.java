/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 *
 * @author x1213
 */
public class Group {
    private class Node {
        private List<Object> objList;
        private List<Node> childList;
        private Node parent;
        public Node() {
            parent = null;
            objList = new ArrayList<Object>();
            childList= new ArrayList<Node>();
        }
        public void addObj(Object obj) {
            objList.add(obj);
        }
        public void removeObj(Object obj) {
            objList.remove(obj);
        }
        public void addChild(Node s) {
            childList.add(s);
        }
        public void removeChild(Node s) {
            childList.remove(s);
        }
        public Node getParent() {
            return parent;
        }
        public void setParent(Node s) {
            parent = s;
        }
        public void unGroup() {
            //cannot ungroup root
            if (parent == null) return;
            for (Object obj: objList) {
                parent.addObj(obj);
            }
            for (Node s: childList) {
                parent.addChild(s);
                s.setParent(parent);
            }
        }
        public void recursivelyAddToList(List<Object> objs) {
            for (Object obj: objList) {
                objs.add(obj);
            }
            for (Node s: childList) {   
                s.recursivelyAddToList(objs);
            }         
        }
        public List<Object> getObjList() {
            return objList;
        }
        public List<Node> getChildList() {
            return childList;
        }
    }
 
    private Hashtable<Object, Node> nodeTable;
    private Node root;
    
    private Node retrieveNode(Object obj) {
        return nodeTable.get(obj);
    }
    private Node retrieveTopTierNode(Object obj) {
        Node s = nodeTable.get(obj);
        if (s == null) {
            return s;
        }
        while (true) {
            if (s.getParent() != root) {
                s = s.getParent();
            }
            else {
                return s;
            }
        }
    }    
    public Group() {
        root = new Node();
        nodeTable = new Hashtable<Object, Node>();
    }
    
    //MUST BE CALLED whenever an object is removed from the automata.
    public void removeObj(Object obj) {
        Node s = retrieveNode(obj);
        if (s == null) return;
        s.removeObj(obj);
        nodeTable.remove(obj);
    }
    public void addObj(Object obj, Node s) {
        nodeTable.put(obj, s);
    }

    //group the objects.
    public void groupObjs(List<Object> objs){
        //list of exsiting groups
        List<Node> nodeList;
        //list of ungrouped objs
        List<Object> objList;
        nodeList = new ArrayList<Node>();
        objList = new ArrayList<Object>();
        //set the 2 lists.
        for (Object obj: objs) {
            Node s = retrieveTopTierNode(obj);
            if (s == null) {
                objList.add(obj);
            }
            else if (!nodeList.contains(s)) {
                nodeList.add(s);
            }
        }
        //check if we need to create a new node
        if (nodeList.size() + objList.size() <= 1) return;
        //create a new group.
        Node newNode = new Node();
        newNode.setParent(root);
        root.addChild(newNode);
        for (Object obj: objList) {
            newNode.addObj(obj);
            addObj(obj, newNode);
        }
        for (Node s: nodeList) {
            newNode.addChild(s);   
            s.setParent(newNode);
        }
    }
    
    //ungroup the objects.
    //we only ungroup the top-tier nodes.
    public void unGroupObjs(List<Object> objs){
        //list of exsiting groups
        List<Node> nodeList;
        nodeList = new ArrayList<Node>();
        //set the 2 lists.
        for (Object obj: objs) {
            Node s = retrieveTopTierNode(obj);
            if (s != null && !nodeList.contains(s)) {
                nodeList.add(s);
            }
        }
        //check if we need to ungroup
        if (nodeList.isEmpty()) {
            return;
        }
        //ungroup each node
        for (Node s: nodeList) {
            List<Object> removedList = s.getObjList();
            for (Object obj: removedList) {
                nodeTable.remove(obj);
            }
            s.unGroup();
        }
    }
    
    //get the selected objs by considering the groups.
    public List<Object> retrieveSelectedObjs(List<Object> objs) {
        //the new objlist
        List<Object> objList;
        //the top-tier nodes
        List<Node> nodeList;
        objList = new ArrayList<Object>();
        nodeList = new ArrayList<Node>();
        //set the 2 lists.
        for (Object obj: objs) {
            Node s = retrieveTopTierNode(obj);
            if (s == null) {
                objList.add(obj);
            }
            else if (!nodeList.contains(s)) {
                nodeList.add(s);
            }
        }
        //update the new list
        for (Node s: nodeList) {
            s.recursivelyAddToList(objList);
        }      
        //return the new list
        return objList;
    }
    
    public List<Object> retrieveAllSelectedObjs() {
        List<Object> objList;  
        objList = new ArrayList<Object>();
        for (Node s: root.getChildList()) {
            s.recursivelyAddToList(objList);
        }    
        return objList;
    }
}
