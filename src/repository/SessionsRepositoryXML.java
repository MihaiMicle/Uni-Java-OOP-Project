package repository;

import domain.Session;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;

public class SessionsRepositoryXML extends FileRepository<Integer, Session> {

    public SessionsRepositoryXML(String filename) {
        super(filename);
    }

    @Override
    protected void readFromFile() {
        this.map = new HashMap<>();
        File file = new File(filename);
        if (!file.exists()) return;

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("session");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    Integer id = Integer.parseInt(getTagValue("id", e));
                    Integer clientId = Integer.parseInt(getTagValue("clientID", e));
                    String date = getTagValue("date", e);
                    String time = getTagValue("time", e);
                    String workout = getTagValue("workout", e);

                    Session session = new Session(id, clientId, date, time, workout);
                    this.map.put(id, session);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeToFile() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("sessions");
            doc.appendChild(rootElement);

            for (Session session : this.map.values()) {
                Element sessionElem = doc.createElement("session");
                rootElement.appendChild(sessionElem);

                addTag(doc, sessionElem, "id", String.valueOf(session.getID()));
                addTag(doc, sessionElem, "clientID", String.valueOf(session.getClientID()));
                addTag(doc, sessionElem, "date", session.getDate());
                addTag(doc, sessionElem, "time", session.getTime());
                addTag(doc, sessionElem, "workout", session.getWorkoutDescription());
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filename));

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTagValue(String tag, Element element) {
        return element.getElementsByTagName(tag).item(0).getTextContent();
    }

    private void addTag(Document doc, Element parent, String tagName, String text) {
        Element elem = doc.createElement(tagName);
        elem.appendChild(doc.createTextNode(text));
        parent.appendChild(elem);
    }
}