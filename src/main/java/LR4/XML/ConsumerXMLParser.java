package LR4.XML;


import LR4.cfgClasses.ConsumerCFGClass;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ConsumerXMLParser {

    public static ConsumerCFGClass parser(String name) {

        ConsumerCFGClass cfg = null;
        {
            try {
                JAXBContext context = JAXBContext.newInstance(ConsumerCFGClass.class);
                Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
                cfg = (ConsumerCFGClass) jaxbUnmarshaller.unmarshal(new File("C:/Users/Евгений/IdeaProjects/JadeLabs/src/main/java/LR4/XML/" + name + ".xml"));
//                System.out.println(cfg.toString());
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

        ConsumerCFGClass cfgClass = WorkWithConfigGFiles.unMarshalAny(ConsumerCFGClass.class, "C:/Users/Евгений/IdeaProjects/JadeLabs/src/main/java/LR4/XML/" + name + ".xml");
//        System.out.println(cfgClass.toString());

        return cfg;
    }

}