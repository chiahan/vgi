/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.display;

import com.mxgraph.util.mxConstants;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import vgi.automata.IniFinGeometricData;
import vgi.automata.StateDrawingData;
import vgi.automata.StateGeometricData;
import vgi.automata.TransitionDrawingData;
import vgi.fsmxml.FsmXmlInterface;
import vgi.fsmxml.FsmXmlInterface.FsmXmlException;

/**
 *
 * @author reng
 */




public class PreferenceStyleXml {
    private static final String TAG_DEFAULT_STYLES="defaultStyles";
    private static final String TAG_STATE_DRAWING_DATA="stateDrawingData";
    private static final String TAG_STATE_GEOMETRIC_DATA="stateGeometricData";
    private static final String TAG_TRANSITION_DRAWING_DATA="transitionDrawingData";
    private static final String TAG_INITIAL_GEOMETRIC_DATA="initialGeometricData";
    private static final String TAG_FINAL_GEOMETRIC_DATA="finalGeometricData";
    
    
    private static final String ATR_FILL_COLOR = "fillColor";
    private static final String ATR_STROKE_COLOR = "strokeColor";
    private static final String ATR_STROKE_WIDTH = "strokeWidth";
    private static final String ATR_START_ARROW = "startArrow";
    private static final String ATR_END_ARROW = "endArrow";
    private static final String ATR_EDGE_STYLE = "edgeStyle";
    private static final String ATR_SHAPE = "shape";
    private static final String ATR_STATE_WIDTH="stateWidth";
    private static final String ATR_STATE_HEIGHT="stateHeight";
    private static final String ATR_STATE_SHAPE="stateShape";
    private static final String ATR_DIRECTION="direction";
    private static final String ATR_LENGTHRATIO="lengthRatio";
    private static final String ATR_LABEL_POS="labelPos";
    private static final String ATR_LABEL_DIST="labelDist";
    private static final String ATR_LABEL_OFFSET_X="labelOffsetx";
    private static final String ATR_LABEL_OFFSET_Y="labelOffsety";
    
    
    
    
    public StateDrawingData read(File xmlFile) throws FileNotFoundException, FsmXmlException{
        
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = null;
        InputStream inputStream = new FileInputStream(xmlFile);
	StateDrawingData sdd;
        try {
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
            int eventType = xmlStreamReader.getEventType();
            if (eventType != XMLStreamReader.START_DOCUMENT) {
                    throw new FsmXmlException("Unrecognizable FSM XML file.");
            }
            
            sdd=parseStateDrawingData(xmlStreamReader);
            
            
        }catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
	} finally {
            if (xmlStreamReader != null) {
                    try {
                            xmlStreamReader.close();
                    } catch (XMLStreamException xmlStreamException) {
                            throw new FsmXmlException(xmlStreamException);
                    }
            } 
	}
        
        return sdd;
        
        
    }
    /* read default styles
     * return in an array: state dd, state gd, transition dd
     */
    public Object[] readAllData(File xmlFile) throws FileNotFoundException, FsmXmlException{
        
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLStreamReader xmlStreamReader = null;
        InputStream inputStream = new FileInputStream(xmlFile);
	StateDrawingData sdd=null;
        StateGeometricData sgd=null;
        TransitionDrawingData tdd=null;
        IniFinGeometricData igd=null;
        IniFinGeometricData fgd=null;
        
        try {
            xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);

            sdd=parseStateDrawingData(xmlStreamReader);
            //System.out.println("read: "+sdd.getFillColor()+" "+sdd.getStrokeColor()+" "+sdd.getStrokeWidth());
            
            sgd=parseStateGeometricData(xmlStreamReader);
            //System.out.println("read: "+sgd.getShape());
        
            
            tdd=parseTransitionDrawingData(xmlStreamReader);
            //System.out.println("read: "+tdd.getStrokeColor()+" "+tdd.getStrokeWidth());
            
            igd=parseIniFinGeometricData(xmlStreamReader,true);
            fgd=parseIniFinGeometricData(xmlStreamReader,false);
            
        }catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
	} finally {
            if (xmlStreamReader != null) {
                    try {
                            xmlStreamReader.close();
                    } catch (XMLStreamException xmlStreamException) {
                            throw new FsmXmlException(xmlStreamException);
                    }
            } 
	}
        
        return new Object[]{sdd,sgd,tdd,igd,fgd};
        
        
    }
    public void write(StateDrawingData dd,File xmlFile) throws FsmXmlException, FileNotFoundException, IOException{
    
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream byteArrayInputStream = null;
                OutputStream outputStream = new FileOutputStream(xmlFile);
		        
		try {
			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(byteArrayOutputStream);
			
                        //xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(outputStream);
			xmlStreamWriter.writeStartDocument();
                        
                        
                        writeStateDrawingData(xmlStreamWriter,dd);
                        
                        
                        
                        xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.flush();
                        
                        
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			
                        Source source = new StreamSource(byteArrayInputStream);
			Result result = new StreamResult(outputStream);
		
                        String originalLineSeparator = System.getProperty("line.separator");
			System.setProperty("line.separator", "\n");
			transformer.transform(source, result);
			System.setProperty("line.separator", originalLineSeparator);
                        
                } catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
		} catch (TransformerConfigurationException transformerConfigurationException) {
			throw new FsmXmlException(transformerConfigurationException);
		} catch (TransformerException transformerException) {
			throw new FsmXmlException(transformerException);
		}  finally {
			if (xmlStreamWriter != null) {
				try {
					xmlStreamWriter.close();
				} catch (XMLStreamException xmlStreamException) {
					throw new FsmXmlInterface.FsmXmlException(xmlStreamException);
				}
			}  // End if (xmlStreamReader != null)
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException iOException) {
					throw new FsmXmlInterface.FsmXmlException(iOException);
				}
			}  // End if (byteArrayInputStream != null)
			try {
				byteArrayOutputStream.close();
			} catch (IOException iOException) {
				throw new FsmXmlInterface.FsmXmlException(iOException);
			}
                        
                        outputStream.close();
		}  // End finally

        
    }
     public void write(StateDrawingData sdd,StateGeometricData sgd,TransitionDrawingData tdd,
                        IniFinGeometricData igd,IniFinGeometricData fgd,
                        File xmlFile) throws FsmXmlException, FileNotFoundException, IOException{
    
                XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = null;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ByteArrayInputStream byteArrayInputStream = null;
                
                OutputStream outputStream = new FileOutputStream(xmlFile);
		        
		try {
			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(byteArrayOutputStream);
			
                        //xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(outputStream);
			xmlStreamWriter.writeStartDocument();
                        xmlStreamWriter.writeStartElement(TAG_DEFAULT_STYLES);
                        
                        if(sdd!=null) writeStateDrawingData(xmlStreamWriter,sdd);
                        if(sgd!=null) writeStateGeometricData(xmlStreamWriter,sgd);
                        if(tdd!=null) writeTransitionDrawingData(xmlStreamWriter,tdd);
                        if(igd!=null) writeInitialFinalGeometricData(xmlStreamWriter,igd,true);
                        if(fgd!=null) writeInitialFinalGeometricData(xmlStreamWriter,fgd,false);
                        
                        xmlStreamWriter.writeEndElement();
                        xmlStreamWriter.writeEndDocument();
			xmlStreamWriter.flush();
                        
                        
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        
                        byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			
                        Source source = new StreamSource(byteArrayInputStream);
			Result result = new StreamResult(outputStream);
		
                        String originalLineSeparator = System.getProperty("line.separator");
			System.setProperty("line.separator", "\n");
			transformer.transform(source, result);
			System.setProperty("line.separator", originalLineSeparator);
                        
                } catch (XMLStreamException xmlStreamException) {
			throw new FsmXmlException(xmlStreamException);
		} catch (TransformerConfigurationException transformerConfigurationException) {
			throw new FsmXmlException(transformerConfigurationException);
		} catch (TransformerException transformerException) {
			throw new FsmXmlException(transformerException);
		}  finally {
			if (xmlStreamWriter != null) {
				try {
					xmlStreamWriter.close();
				} catch (XMLStreamException xmlStreamException) {
					throw new FsmXmlInterface.FsmXmlException(xmlStreamException);
				}
			}  // End if (xmlStreamReader != null)
			if (byteArrayInputStream != null) {
				try {
					byteArrayInputStream.close();
				} catch (IOException iOException) {
					throw new FsmXmlInterface.FsmXmlException(iOException);
				}
			}  // End if (byteArrayInputStream != null)
			try {
				byteArrayOutputStream.close();
			} catch (IOException iOException) {
				throw new FsmXmlInterface.FsmXmlException(iOException);
			}
                        
                        outputStream.close();
		}  // End finally

        
    }
    public void writeStateDrawingData(XMLStreamWriter xmlStreamWriter,StateDrawingData sdd) throws XMLStreamException{
        
        xmlStreamWriter.writeStartElement(TAG_STATE_DRAWING_DATA);
        
        xmlStreamWriter.writeAttribute(ATR_FILL_COLOR, sdd.getFillColor());
	xmlStreamWriter.writeAttribute(ATR_STROKE_COLOR, sdd.getStrokeColor());
	xmlStreamWriter.writeAttribute(ATR_STROKE_WIDTH, String.valueOf(sdd.getStrokeWidth()));
			
        xmlStreamWriter.writeEndElement();
        
    }
    public void writeStateGeometricData(XMLStreamWriter xmlStreamWriter,StateGeometricData sgd) throws XMLStreamException{
        
        xmlStreamWriter.writeStartElement(TAG_STATE_GEOMETRIC_DATA);
        
        xmlStreamWriter.writeAttribute(ATR_STATE_WIDTH, String.valueOf(sgd.getWidth()));
	xmlStreamWriter.writeAttribute(ATR_STATE_HEIGHT, String.valueOf(sgd.getHeight()));
	xmlStreamWriter.writeAttribute(ATR_STATE_SHAPE, sgd.getShape());
	
        xmlStreamWriter.writeEndElement();
        
    }
    public void writeTransitionDrawingData(XMLStreamWriter xmlStreamWriter,TransitionDrawingData tdd) throws XMLStreamException{
        
        xmlStreamWriter.writeStartElement(TAG_TRANSITION_DRAWING_DATA);
        
        xmlStreamWriter.writeAttribute(ATR_STROKE_COLOR, tdd.getStrokeColor());
	xmlStreamWriter.writeAttribute(ATR_STROKE_WIDTH, String.valueOf(tdd.getStrokeWidth()));
	xmlStreamWriter.writeAttribute(ATR_START_ARROW, tdd.getStartArrow());
	xmlStreamWriter.writeAttribute(ATR_END_ARROW, tdd.getEndArrow());
			
        xmlStreamWriter.writeEndElement();
        
    }
    void writeInitialFinalGeometricData(XMLStreamWriter xmlStreamWriter,IniFinGeometricData igd,boolean isIni) throws XMLStreamException{
        if(isIni)   xmlStreamWriter.writeStartElement(TAG_INITIAL_GEOMETRIC_DATA);
        else     xmlStreamWriter.writeStartElement(TAG_FINAL_GEOMETRIC_DATA);
        
        xmlStreamWriter.writeAttribute(ATR_DIRECTION, String.valueOf(igd.direction));
	xmlStreamWriter.writeAttribute(ATR_LENGTHRATIO, String.valueOf(igd.lengthRatio));
	xmlStreamWriter.writeAttribute(ATR_LABEL_POS, String.valueOf(igd.labelPosAndDist.x));
	xmlStreamWriter.writeAttribute(ATR_LABEL_DIST, String.valueOf(igd.labelPosAndDist.y));
        xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_X, String.valueOf(igd.labelOffset.x));
	xmlStreamWriter.writeAttribute(ATR_LABEL_OFFSET_Y, String.valueOf(igd.labelOffset.y));
        
        xmlStreamWriter.writeEndElement();
       
    }
    public StateDrawingData parseStateDrawingData(XMLStreamReader xmlStreamReader) throws XMLStreamException{
        //xmlStreamReader.
        while (xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();
            //if (eventType == XMLStreamReader.START_ELEMENT) {
            if(xmlStreamReader.isStartElement()){
                if(xmlStreamReader.getLocalName()==TAG_STATE_DRAWING_DATA){
                        //System.out.println(xmlStreamReader.getLocation());
                        break;
                    }
            }
        }
        //System.out.println(xmlStreamReader.getAttributeCount());
        String fillcolor=xmlStreamReader.getAttributeValue(null, ATR_FILL_COLOR);
        String strokecolor=xmlStreamReader.getAttributeValue(null, ATR_STROKE_COLOR);
        float strokewidth=Float.valueOf(xmlStreamReader.getAttributeValue(null, ATR_STROKE_WIDTH));
        
        StateDrawingData sdd=new StateDrawingData(fillcolor,strokecolor,strokewidth);
        return sdd;
    }
    public StateGeometricData parseStateGeometricData(XMLStreamReader xmlStreamReader) throws XMLStreamException{
                        
        while (xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();
            if (eventType == XMLStreamReader.START_ELEMENT) {
                    if(xmlStreamReader.getLocalName()==TAG_STATE_GEOMETRIC_DATA) break;
            }		
        }
        String shape=xmlStreamReader.getAttributeValue(null, ATR_STATE_SHAPE);
        float width=Float.valueOf(xmlStreamReader.getAttributeValue(null, ATR_STATE_WIDTH));
        float height=Float.valueOf(xmlStreamReader.getAttributeValue(null, ATR_STATE_HEIGHT));
        
        
        StateGeometricData sgd=new StateGeometricData();
        sgd.setShape(shape);
        //sgd.setSize(new Point2D.Double(width,height));
        sgd.setWidth(width);
        sgd.setHeight(height);
        return sgd;
    }


    /**
     * Parse transition drawing attributes from given default style xml file
     * @param xmlStreamReader
     * @return
     * @throws XMLStreamException 
     */
    public TransitionDrawingData parseTransitionDrawingData(XMLStreamReader xmlStreamReader)
            throws XMLStreamException {

        // keep getting xmlstream till end of xml file
        while ( xmlStreamReader.hasNext() ) {

            int eventType = xmlStreamReader.next();
            if (eventType == XMLStreamReader.START_ELEMENT) {
                    if (xmlStreamReader.getLocalName() == TAG_TRANSITION_DRAWING_DATA) {
                        break;
                    }
            }

        }

        String strokeColor = xmlStreamReader.getAttributeValue(null,
                                                            ATR_STROKE_COLOR);
        float strokeWidth = Float.valueOf(xmlStreamReader.getAttributeValue(null,
                                                            ATR_STROKE_WIDTH));
        String startArrow = xmlStreamReader.getAttributeValue(null,
                                                            ATR_START_ARROW);
        String endArrow = xmlStreamReader.getAttributeValue(null,
                                                            ATR_END_ARROW);
        String edgeStyle = xmlStreamReader.getAttributeValue(null,
                                                            ATR_EDGE_STYLE);
        String shape = xmlStreamReader.getAttributeValue(null,
                                                            ATR_SHAPE);
        
        TransitionDrawingData tdd = new TransitionDrawingData(strokeColor,
                                                            strokeWidth,
                                                            startArrow,
                                                            endArrow,
                                                            edgeStyle,
                                                            shape);
        return tdd;

    }


    IniFinGeometricData parseIniFinGeometricData(XMLStreamReader xmlStreamReader,boolean isIni) throws XMLStreamException{
        if(isIni){
        while (xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();
            if (eventType == XMLStreamReader.START_ELEMENT) {
                if(xmlStreamReader.getLocalName()==TAG_INITIAL_GEOMETRIC_DATA) break;
            }
        }
        }else{
        while (xmlStreamReader.hasNext()) {
            int eventType = xmlStreamReader.next();
            if (eventType == XMLStreamReader.START_ELEMENT) {
                if(xmlStreamReader.getLocalName()==TAG_FINAL_GEOMETRIC_DATA) break;
            }
        }    
        }
        double direction=Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_DIRECTION));
        double lengthRatio=Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_LENGTHRATIO));
        double labelPos=Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_LABEL_POS));
        double labelDist=Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_LABEL_DIST));
        double labelOffsetX=Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_X));
        double labelOffsetY=Double.valueOf(xmlStreamReader.getAttributeValue(null, ATR_LABEL_OFFSET_Y));
        
        IniFinGeometricData igd=new IniFinGeometricData(direction,lengthRatio,new Point2D.Double(labelPos,labelDist)
                ,new Point2D.Double(labelOffsetX,labelOffsetY));
                
        return igd;
    }


    /**
     * The main execution method of PreferenceStyleXml class
     * @param args
     * @throws FileNotFoundException
     * @throws vgi.fsmxml.FsmXmlInterface.FsmXmlException 
     */
    public static void main(String args[]) throws FileNotFoundException,
                                                    FsmXmlException {

        File defaultStyle = new File( "defaultStyle.xml" );
        File testDefaultStyle = new File( "testdefaultStyle.xml" );
        
        StateDrawingData sdd = new StateDrawingData("#C3D9FF","#6482B9",1);
        StateGeometricData sgd = new StateGeometricData();
        sgd.setSize( new Point2D.Double(50, 50) );
        sgd.setShape( "ellipse" );

        TransitionDrawingData tdd = new TransitionDrawingData("#6482B9",
                                                            1,
                                                            "none",
                                                            "classic",
                                                            mxConstants.EDGESTYLE_LOOP,
                                                            mxConstants.SHAPE_CURVE);
        
        IniFinGeometricData igd = new IniFinGeometricData(Math.PI,
                                                        0.5,
                                                        new Point2D.Double(0, 0),
                                                        new Point2D.Double(0, 0));
        IniFinGeometricData fgd = new IniFinGeometricData(0,
                                                        0.5,
                                                        new Point2D.Double(0, 0),
                                                        new Point2D.Double(0, 0));
        
        PreferenceStyleXml psx = new PreferenceStyleXml();

        try {

            psx.write(sdd, sgd, tdd, igd, fgd, defaultStyle);
            
        } catch (IOException ex) {

            Logger.getLogger(PreferenceStyleXml.class.getName())
                    .log(Level.SEVERE, null, ex);

        } catch (FsmXmlException ex) {

            Logger.getLogger(PreferenceStyleXml.class.getName())
                    .log(Level.SEVERE, null, ex);

        }
        

        /* Dealing with state geometric and drawing data */
        Object[] data = psx.readAllData(defaultStyle);
        StateDrawingData rsdd = (StateDrawingData)data[0];
        System.out.println("read: "
                + rsdd.getFillColor()
                + " "
                + rsdd.getStrokeColor()
                + " "
                + rsdd.getStrokeWidth());
        StateGeometricData rsgd = (StateGeometricData)data[1];
        System.out.println("read: "
                + rsgd.getShape()
                + " "
                + rsgd.getLocation()
                + " "
                + rsgd.getSize());

    }

}
